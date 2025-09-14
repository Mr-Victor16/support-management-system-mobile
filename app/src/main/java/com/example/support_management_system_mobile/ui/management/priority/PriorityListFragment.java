package com.example.support_management_system_mobile.ui.management.priority;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.payload.response.PriorityResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PriorityListFragment extends Fragment {
    private PriorityManagementViewModel viewModel;
    private PriorityAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private TextView emptyErrorTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_priority_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PriorityManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadPriorities();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.prioritiesRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyErrorTextView = view.findViewById(R.id.emptyErrorTextView);
        fab = view.findViewById(R.id.fabAddPriority);
        fab.setOnClickListener(v -> showPriorityDialog(null));
    }

    private void setupRecyclerView() {
        AuthContext authContext = viewModel.getAuthContext();

        adapter = new PriorityAdapter(new PriorityAdapter.OnPriorityInteractionListener() {
            @Override
            public void onEdit(PriorityResponse priority) {
                showPriorityDialog(priority);
            }

            @Override
            public void onDelete(PriorityResponse priority) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_priority)
                        .setMessage(getString(R.string.confirm_delete_priority_message, priority.name()))
                        .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deletePriority(priority.priorityID()))
                        .setNegativeButton(R.string.cancel_button, null)
                        .show();
            }
        }, authContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.priorityListState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof PriorityListUIState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyErrorTextView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            } else if (state instanceof PriorityListUIState.Success successState) {
                boolean isAdmin = viewModel.getAuthContext().isAdmin();

                progressBar.setVisibility(View.GONE);
                adapter.submitList(successState.priorities);
                fab.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

                if (successState.priorities.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyErrorTextView.setText(R.string.no_priorities_defined);
                    emptyErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyErrorTextView.setVisibility(View.GONE);
                }
            } else if (state instanceof PriorityListUIState.Error) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                emptyErrorTextView.setText(((PriorityListUIState.Error) state).message);
                emptyErrorTextView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showPriorityDialog(@Nullable PriorityResponse priority) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        boolean isEditing = priority != null;
        String title = isEditing ? getString(R.string.edit_priority) : getString(R.string.add_new_priority);
        builder.setTitle(title);

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_priority, null);
        final TextInputLayout nameInputLayout = dialogView.findViewById(R.id.nameInputLayout);
        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);

        if (isEditing) {
            nameEditText.setText(priority.name());
        }
        builder.setView(dialogView);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isPriorityNameValid(s.toString())) {
                    nameInputLayout.setError(null);
                } else {
                    nameInputLayout.setError(getString(R.string.priority_name_error));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        });

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            boolean isNameValid = isPriorityNameValid(name);

            nameInputLayout.setError(isNameValid ? null : getString(R.string.priority_name_error));

            if (isNameValid) {
                if (isEditing) {
                    viewModel.updatePriority(priority.priorityID(), name);
                } else {
                    viewModel.createPriority(name);
                }
                dialog.dismiss();
            }
        });
    }

    public boolean isPriorityNameValid(String name) {
        return name != null && name.length() >= 3 && name.length() <= 20;
    }
}