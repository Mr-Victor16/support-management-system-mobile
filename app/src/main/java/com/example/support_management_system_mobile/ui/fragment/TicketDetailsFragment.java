package com.example.support_management_system_mobile.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.adapter.TicketReplyAdapter;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.models.Image;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.models.TicketReply;
import com.example.support_management_system_mobile.models.User;
import com.example.support_management_system_mobile.payload.request.AddTicketReplyRequest;
import com.example.support_management_system_mobile.ui.activity.TicketFormActivity;
import com.example.support_management_system_mobile.ui.activity.TicketImageActivity;
import com.example.support_management_system_mobile.validators.TicketValidator;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketDetailsFragment extends Fragment {
    private static final String ARG_TICKET = "ticket";
    private static final String ARG_USER = "user";
    private static final String ARG_NEW = "new";
    private TextView ticketTitle, ticketDate, ticketStatus, ticketCategory, ticketDescription;
    private TextView replyCharCountTextView, noRepliesTextView;
    private RecyclerView repliesRecyclerView;
    private EditText replyEditText;
    private Button sendReplyButton, editTicketButton, deleteTicketButton, changeStatusButton, showImagesButton;
    private LinearLayout replyInputLayout, ticketActionsLayout;
    private List<Image> ticketImages = new ArrayList<>();
    private ActivityResultLauncher<Intent> fullScreenImageLauncher;
    private Ticket ticket;
    private User currentUser;

    public TicketDetailsFragment() {

    }

    public static TicketDetailsFragment newInstance(Ticket ticket, User user, Boolean newTicket) {
        TicketDetailsFragment fragment = new TicketDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TICKET, ticket);
        args.putSerializable(ARG_USER, user);
        args.putBoolean(ARG_NEW, newTicket);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ticket = (Ticket) getArguments().getSerializable(ARG_TICKET);
            currentUser = (User) getArguments().getSerializable(ARG_USER);
        }

        fullScreenImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fetchUpdatedTicket();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        ticketTitle = view.findViewById(R.id.ticketTitle);
        ticketDate = view.findViewById(R.id.ticketDate);
        ticketStatus = view.findViewById(R.id.ticketStatus);
        ticketCategory = view.findViewById(R.id.ticketCategory);
        ticketDescription = view.findViewById(R.id.ticketDescription);

        repliesRecyclerView = view.findViewById(R.id.repliesRecyclerView);
        replyEditText = view.findViewById(R.id.replyEditText);
        sendReplyButton = view.findViewById(R.id.sendReplyButton);
        sendReplyButton.setEnabled(false);

        replyInputLayout = view.findViewById(R.id.replyInputLayout);
        ticketActionsLayout = view.findViewById(R.id.ticketActionsLayout);

        editTicketButton = view.findViewById(R.id.editTicketButton);
        deleteTicketButton = view.findViewById(R.id.deleteTicketButton);
        changeStatusButton = view.findViewById(R.id.changeStatusButton);
        noRepliesTextView = view.findViewById(R.id.noRepliesTextView);
        showImagesButton = view.findViewById(R.id.showImagesButton);

        setTicketData();
        if(getArguments().getBoolean(ARG_NEW)) addImagesDialog();
        setupRepliesList(ticket.getReplies());
        setupActionButtons();

        replyCharCountTextView = view.findViewById(R.id.replyCharCountTextView);
        setupReplyInputListeners();

        return view;
    }

    private void addImagesDialog(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Add images")
                .setMessage("Do you want add images to your ticket?")
                .setPositiveButton("Yes", (dialog, which) -> showImages())
                .setNegativeButton("No", null)
                .show();
    }

    private void setupReplyInputListeners() {
        replyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                int length = s.length();

                replyCharCountTextView.setText(length + " / 500");

                boolean isValid = TicketValidator.isTicketReplyValid(text);
                sendReplyButton.setEnabled(isValid);

                if (!isValid) replyEditText.setError(getString(R.string.reply_length_error));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendReplyButton.setOnClickListener(v -> sendReply(replyEditText.getText().toString()));
    }

    private void sendReply(String content){
        String token = "Bearer " + JWTUtils.getToken(requireContext());
        AddTicketReplyRequest request = new AddTicketReplyRequest(ticket.getId(), content);

        APIClient.getAPIService(requireContext()).addTicketReply(request, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), R.string.reply_added, Toast.LENGTH_SHORT).show();
                    fetchUpdatedTicket();
                    replyEditText.setText("");
                } else {
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTicketData() {
        ticketTitle.setText(ticket.getTitle());
        ticketDate.setText(ticket.getCreatedDate().toString());
        ticketStatus.setText(ticket.getStatus().getName());
        ticketCategory.setText(ticket.getCategory().getName());
        ticketDescription.setText(ticket.getDescription());
        ticketImages = ticket.getImages();

        boolean isClosed = ticket.getStatus().isCloseTicket();
        replyInputLayout.setVisibility(isClosed ? View.GONE : View.VISIBLE);

        int imageCount = ticket.getImages().size();
        if (imageCount > 0) showImagesButton.setText(getString(R.string.images_count)+imageCount);
        else showImagesButton.setText(R.string.add_images);
    }

    private void setupRepliesList(List<TicketReply> replies) {
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TicketReplyAdapter adapter = new TicketReplyAdapter(
                replies,
                currentUser.getRole(),
                this::confirmDeleteReply
        );
        repliesRecyclerView.setAdapter(adapter);

        if (replies == null || replies.isEmpty()) {
            noRepliesTextView.setVisibility(View.VISIBLE);
            repliesRecyclerView.setVisibility(View.GONE);
        } else {
            noRepliesTextView.setVisibility(View.GONE);
            repliesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void confirmDeleteReply(TicketReply reply) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_reply)
                .setMessage(R.string.confirm_delete_reply)
                .setPositiveButton(R.string.delete_button, (dialog, which) -> deleteReply(reply))
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void deleteReply(TicketReply reply) {
        String token = "Bearer " + JWTUtils.getToken(requireContext());

        APIClient.getAPIService(requireContext()).deleteReply(reply.getId(), token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), R.string.reply_deleted, Toast.LENGTH_SHORT).show();
                    ticket.getReplies().remove(reply);
                    setupRepliesList(ticket.getReplies());
                } else {
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteTicket() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_ticket)
                .setMessage(R.string.confirm_delete_ticket)
                .setPositiveButton(R.string.delete_button, (dialog, which) -> deleteTicket())
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void deleteTicket() {
        String token = "Bearer " + JWTUtils.getToken(requireContext());

        APIClient.getAPIService(requireContext()).deleteTicket(ticket.getId(), token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), R.string.ticket_deleted, Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupActionButtons() {
        boolean isOwner = currentUser.getId().equals(ticket.getUser().getId());
        String role = currentUser.getRole();
        boolean isOperatorOrAdmin = role.contains("OPERATOR") || role.contains("ADMIN");

        if (isOwner || isOperatorOrAdmin) {
            editTicketButton.setVisibility(View.VISIBLE);
            deleteTicketButton.setVisibility(View.VISIBLE);
            showImagesButton.setVisibility(View.VISIBLE);
        }

        if (isOperatorOrAdmin) {
            changeStatusButton.setVisibility(View.VISIBLE);
        }

        editTicketButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TicketFormActivity.class);
            intent.putExtra("ticket_id", ticket.getId());
            editTicketLauncher.launch(intent);
            //startActivity(intent);
        });

        deleteTicketButton.setOnClickListener(v -> {
            confirmDeleteTicket();
        });

        changeStatusButton.setOnClickListener(v -> {
            // TODO: Show dialog or status picker
            Toast.makeText(getContext(), "Change status", Toast.LENGTH_SHORT).show();
        });

        showImagesButton.setOnClickListener(v -> showImages());
    }

    private void showImages(){
        Intent intent = new Intent(requireContext(), TicketImageActivity.class);
        intent.putExtra(TicketImageActivity.EXTRA_IMAGES, new ArrayList<>(ticketImages));
        intent.putExtra(TicketImageActivity.EXTRA_POSITION, 0);
        intent.putExtra(TicketImageActivity.TICKET_ID, ticket.getId().longValue());
        fullScreenImageLauncher.launch(intent);
    }

    private void fetchUpdatedTicket() {
        String token = "Bearer " + JWTUtils.getToken(requireContext());

        APIClient.getAPIService(requireContext()).getTicketById(ticket.getId(), token).enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticket = response.body();
                    setTicketData();
                    setupRepliesList(ticket.getReplies());
                } else {
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ticket> call, Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> editTicketLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    ticket = (Ticket) result.getData().getSerializableExtra("ticket_object");
                    setTicketData();
                }
            });
}