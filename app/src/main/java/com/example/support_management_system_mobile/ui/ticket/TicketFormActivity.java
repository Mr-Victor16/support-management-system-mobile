package com.example.support_management_system_mobile.ui.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.models.Software;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.payload.request.AddTicketRequest;
import com.example.support_management_system_mobile.payload.request.UpdateTicketRequest;
import com.example.support_management_system_mobile.validators.TicketValidator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketFormActivity extends AppCompatActivity {
    private EditText editTitle, editDescription, editVersion;
    private Spinner spinnerCategory, spinnerPriority, spinnerSoftware;
    private Button btnSave;
    private Long ticketId = null;
    private List<Category> categories = new ArrayList<>();
    private List<Priority> priorities = new ArrayList<>();
    private List<Software> software = new ArrayList<>();
    private Ticket currentTicket;
    private Handler handler;
    private final HashMap<EditText, Boolean> visitedFields = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editVersion = findViewById(R.id.editVersion);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        spinnerSoftware = findViewById(R.id.spinnerSoftware);
        btnSave = findViewById(R.id.btnSave);
        handler = new Handler();

        ticketId = getIntent().hasExtra("ticket_id") ? getIntent().getLongExtra("ticket_id", -1) : null;

        TextView formHeader = findViewById(R.id.formHeader);
        formHeader.setText(ticketId != null ? R.string.edit_ticket : R.string.new_ticket);

        btnSave.setEnabled(false);
        fetchAllData();

        btnSave.setOnClickListener(v -> saveTicket());
        setupTextListeners();
    }

    private void setupTextListeners() {
        for (EditText editText : new EditText[]{editTitle, editDescription, editVersion}) {
            editText.addTextChangedListener(createTextWatcher(editText));
        }
    }

    private void fetchCategories(String token){
        APIClient.getAPIService(this).getAllCategories(token).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(TicketFormActivity.this, android.R.layout.simple_spinner_item, categories);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(TicketFormActivity.this, "Brak możliwości obsłużenia wskazanego żądania", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(TicketFormActivity.this, "Brak możliwości obsłużenia wskazanego żądania", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchSoftware(String token){
        APIClient.getAPIService(this).getAllSoftware(token).enqueue(new Callback<List<Software>>() {
            @Override
            public void onResponse(Call<List<Software>> call, Response<List<Software>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    software = response.body();
                    ArrayAdapter<Software> adapter = new ArrayAdapter<>(TicketFormActivity.this, android.R.layout.simple_spinner_item, software);
                    spinnerSoftware.setAdapter(adapter);
                } else {
                    Toast.makeText(TicketFormActivity.this, "Brak możliwości obsłużenia wskazanego żądania", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override public void onFailure(Call<List<Software>> call, Throwable t) {
                Toast.makeText(TicketFormActivity.this, "Brak możliwości obsłużenia wskazanego żądania", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchPriorities(String token){
        APIClient.getAPIService(this).getAllPriorities(token).enqueue(new Callback<List<Priority>>() {
            @Override
            public void onResponse(Call<List<Priority>> call, Response<List<Priority>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    priorities = response.body();
                    setupPrioritySpinner();
                } else {
                    Toast.makeText(TicketFormActivity.this, "Brak możliwości obsłużenia wskazanego żądania", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Priority>> call, Throwable t) {
                Toast.makeText(TicketFormActivity.this, "Brak możliwości obsłużenia wskazanego żądania", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void fetchAllData() {
        String token = "Bearer " + JWTUtils.getToken(this);

        fetchCategories(token);
        fetchSoftware(token);
        fetchPriorities(token);

        if (ticketId != null && ticketId > 0) {
            APIClient.getAPIService(this).getTicketById(ticketId, token).enqueue(new Callback<Ticket>() {
                @Override
                public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                    if (response.isSuccessful()) {
                        currentTicket = response.body();
                        populateFields(currentTicket);
                        btnSave.setEnabled(true);
                    }
                }

                @Override public void onFailure(Call<Ticket> call, Throwable t) {
                    Toast.makeText(TicketFormActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupPrioritySpinner() {
        ArrayAdapter<Priority> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void populateFields(Ticket ticket) {
        editTitle.setText(ticket.getTitle());
        editDescription.setText(ticket.getDescription());
        editVersion.setText(ticket.getVersion());

        selectSpinnerItem(spinnerCategory, ticket.getCategory());
        selectSpinnerItem(spinnerPriority, ticket.getPriority());
        selectSpinnerItem(spinnerSoftware, ticket.getSoftware());
    }

    private <T> void selectSpinnerItem(Spinner spinner, T value) {
        ArrayAdapter<T> adapter = (ArrayAdapter<T>) spinner.getAdapter();
        if (spinner.getAdapter() == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveTicket() {
        String token = "Bearer " + JWTUtils.getToken(this);

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        Long categoryId = selectedCategory != null ? selectedCategory.getId() : null;

        Priority selectedPriority = (Priority) spinnerPriority.getSelectedItem();
        Long priorityId = selectedPriority != null ? selectedPriority.getId() : null;

        Software selectedSoftware = (Software) spinnerSoftware.getSelectedItem();
        Long softwareId = selectedSoftware != null ? selectedSoftware.getId() : null;

        if (ticketId == null || ticketId <= 0) {
            AddTicketRequest request = new AddTicketRequest(
                    editTitle.getText().toString(),
                    editDescription.getText().toString(),
                    categoryId,
                    priorityId,
                    editVersion.getText().toString(),
                    softwareId
            );

            APIClient.getAPIService(this).createTicket(request, token).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(TicketFormActivity.this, "Utworzono ticket", Toast.LENGTH_SHORT).show();
                    navigateToTicketDetails(true);
                }

                @Override public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(TicketFormActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            UpdateTicketRequest request = new UpdateTicketRequest(
                    ticketId,
                    editTitle.getText().toString(),
                    editDescription.getText().toString(),
                    categoryId,
                    priorityId,
                    editVersion.getText().toString(),
                    softwareId
            );

            APIClient.getAPIService(this).updateTicket(request, token).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(TicketFormActivity.this, "Zaktualizowano ticket", Toast.LENGTH_SHORT).show();
                    navigateToTicketDetails(false);
                }

                @Override public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(TicketFormActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void validateForm() {
        boolean valid = true;

        valid &= validateField(editTitle, TicketValidator::isTicketTitleValid, R.string.ticket_title_error);
        valid &= validateField(editDescription, TicketValidator::isTicketDescriptionValid, R.string.ticket_description_error);
        valid &= validateField(editVersion, TicketValidator::isTicketVersionValid, R.string.software_version_error);

        btnSave.setEnabled(valid);
    }

    private boolean validateField(EditText field, Predicate<String> validator, int errorMsgResId) {
        String input = field.getText().toString().trim();
        boolean isValid = validator.test(input);

        if (!isValid && Boolean.TRUE.equals(visitedFields.get(field))) {
            field.setError(getString(errorMsgResId));
        } else {
            field.setError(null);
        }

        return isValid;
    }

    private TextWatcher createTextWatcher(EditText editText) {
        return new TextWatcher() {
            private Runnable runnable;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!visitedFields.containsKey(editText)) {
                    visitedFields.put(editText, true);
                }

                if (runnable != null) handler.removeCallbacks(runnable);
                runnable = () -> validateForm();
                handler.postDelayed(runnable, 300);
            }

            @Override public void afterTextChanged(Editable s) {}
        };
    }

    private void navigateToTicketDetails(Boolean newTicket) {
        String token = "Bearer " + JWTUtils.getToken(this);

        if (newTicket){
            APIClient.getAPIService(this).getUserTickets(token).enqueue(new Callback<List<Ticket>>() {
                @Override
                public void onResponse(Call<List<Ticket>> call, Response<List<Ticket>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Ticket lastAddedTicket = response.body().stream()
                                .max(Comparator.comparing(Ticket::getId))
                                .orElseThrow(() -> new NoSuchElementException("Lista jest pusta"));

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("ticket_added", newTicket);
                        resultIntent.putExtra("ticket_object", lastAddedTicket);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(TicketFormActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Ticket>> call, Throwable t) {
                    Toast.makeText(TicketFormActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            APIClient.getAPIService(this).getTicketById(ticketId, token).enqueue(new Callback<Ticket>() {
                @Override
                public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                    if (response.isSuccessful()) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("ticket_added", newTicket);
                        resultIntent.putExtra("ticket_object", response.body());
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }

                @Override public void onFailure(Call<Ticket> call, Throwable t) {
                    Toast.makeText(TicketFormActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}