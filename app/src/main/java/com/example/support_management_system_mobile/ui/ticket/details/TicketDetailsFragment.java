package com.example.support_management_system_mobile.ui.ticket.details;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.models.Status;
import com.example.support_management_system_mobile.data.models.Ticket;
import com.example.support_management_system_mobile.data.models.TicketReply;
import com.example.support_management_system_mobile.ui.ticket.form.TicketFormFragment;
import com.example.support_management_system_mobile.ui.ticket.TicketViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TicketDetailsFragment extends Fragment {
    private TicketViewModel viewModel;
    private TicketReplyAdapter replyAdapter;
    private long ticketId = -1;

    private ProgressBar progressBar;
    private NestedScrollView contentLayout;
    private TextView errorMessageTextView;
    private TextView ticketTitle, ticketStatus, closedTicketNotice, ticketDescription, ticketCategory, ticketPriority, ticketSoftware, ticketAuthor, ticketDate, noRepliesTextView;
    private Button sendReplyButton;
    private ImageButton editTicketButton, deleteTicketButton, changeStatusButton, manageImagesButton;
    private RecyclerView repliesRecyclerView;
    private EditText replyEditText;
    private TextInputLayout replyTextInputLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ticketId = getArguments().getLong("ticketId", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_details, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);

        setupRecyclerView();
        setupClickListeners();
        observeViewModel();

        if (ticketId > 0) {
            viewModel.loadTicketDetails(ticketId);
        } else {
            showErrorState(R.string.ticket_not_found);
        }
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        contentLayout = view.findViewById(R.id.contentLayout);
        errorMessageTextView = view.findViewById(R.id.errorMessageTextView);
        ticketTitle = view.findViewById(R.id.ticketTitle);
        ticketStatus = view.findViewById(R.id.ticketStatus);
        closedTicketNotice = view.findViewById(R.id.closedTicketNotice);
        ticketDescription = view.findViewById(R.id.ticketDescription);
        ticketCategory = view.findViewById(R.id.ticketCategory);
        ticketPriority = view.findViewById(R.id.ticketPriority);
        ticketSoftware = view.findViewById(R.id.ticketSoftware);
        ticketAuthor = view.findViewById(R.id.ticketAuthor);
        ticketDate = view.findViewById(R.id.ticketDate);
        editTicketButton = view.findViewById(R.id.editTicketButton);
        deleteTicketButton = view.findViewById(R.id.deleteTicketButton);
        changeStatusButton = view.findViewById(R.id.changeStatusButton);
        manageImagesButton = view.findViewById(R.id.manageImagesButton);
        repliesRecyclerView = view.findViewById(R.id.repliesRecyclerView);
        noRepliesTextView = view.findViewById(R.id.noRepliesTextView);
        replyTextInputLayout = view.findViewById(R.id.replyTextInputLayout);
        replyEditText = view.findViewById(R.id.replyEditText);
        sendReplyButton = view.findViewById(R.id.sendReplyButton);
    }

    private void setupRecyclerView() {
        replyAdapter = new TicketReplyAdapter(this::showDeleteReplyDialog);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        repliesRecyclerView.setAdapter(replyAdapter);
    }

    private void setupClickListeners() {
        sendReplyButton.setOnClickListener(v -> viewModel.addReply());
        editTicketButton.setOnClickListener(v -> viewModel.onEditTicketClicked());
        manageImagesButton.setOnClickListener(v -> viewModel.onManageImagesClicked());
        changeStatusButton.setOnClickListener(v -> viewModel.loadStatuses());
        deleteTicketButton.setOnClickListener(v -> showDeleteTicketDialog());

        replyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.replyContent.setValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.ticketDetailsState.observe(getViewLifecycleOwner(), this::updateUIForState);
        viewModel.isReplyValid.observe(getViewLifecycleOwner(), isValid -> sendReplyButton.setEnabled(isValid));

        viewModel.getStatusesEvent().observe(getViewLifecycleOwner(), event -> {
            List<Status> statuses = event.getContentIfNotHandled();
            if (statuses != null && !statuses.isEmpty()) {
                showStatusChangeDialog(statuses);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        viewModel.detailsNavigation.observe(getViewLifecycleOwner(), event -> {
            TicketViewModel.DetailsNavigation target = event.getContentIfNotHandled();
            if (target != null) handleNavigation(target);
        });

        viewModel.replyContent.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(replyEditText.getText().toString(), text)) {
                replyEditText.setText(text);
            }
        });
    }

    private void updateUIForState(TicketDetailsUIState state) {
        progressBar.setVisibility(state instanceof TicketDetailsUIState.Loading ? View.VISIBLE : View.GONE);
        contentLayout.setVisibility(state instanceof TicketDetailsUIState.Success ? View.VISIBLE : View.GONE);
        errorMessageTextView.setVisibility(state instanceof TicketDetailsUIState.Error ? View.VISIBLE : View.GONE);

        if (state instanceof TicketDetailsUIState.Success) {
            bindSuccessState((TicketDetailsUIState.Success) state);
        } else if (state instanceof TicketDetailsUIState.Error) {
            errorMessageTextView.setText(((TicketDetailsUIState.Error) state).messageTextResId);
        }
    }

    private void bindSuccessState(TicketDetailsUIState.Success state) {
        bindTicketData(state.ticket);
        bindControlStates(state.controls, state.imageCount, state.isClosedNoticeVisible);
        bindReplies(state.ticket.getReplies(), state.controls.canDeleteReply());
    }

    private void bindTicketData(Ticket ticket) {
        ticketTitle.setText(ticket.getTitle());
        ticketStatus.setText(ticket.getStatus().getName());
        ticketDescription.setText(ticket.getDescription());
        ticketCategory.setText(getString(R.string.category_format, ticket.getCategory().getName()));
        ticketPriority.setText(getString(R.string.priority_format, ticket.getPriority().getName()));
        ticketSoftware.setText(getString(R.string.software_format, ticket.getSoftware().getName(), ticket.getVersion()));
        ticketAuthor.setText(getString(R.string.author_format, ticket.getUser().getUsername()));
        ticketDate.setText(getString(R.string.date_format, ticket.getCreatedDate().toString()));
    }

    private void bindControlStates(TicketDetailsControlsState controls, int imageCount, boolean isClosedNoticeVisible) {
        editTicketButton.setVisibility(controls.canEditTicket() ? View.VISIBLE : View.GONE);
        deleteTicketButton.setVisibility(controls.canDeleteTicket() ? View.VISIBLE : View.GONE);
        changeStatusButton.setVisibility(controls.canChangeStatus() ? View.VISIBLE : View.GONE);
        manageImagesButton.setVisibility(controls.canViewImages() ? View.VISIBLE : View.GONE);
        replyTextInputLayout.setVisibility(controls.canAddReply() ? View.VISIBLE : View.GONE);
        closedTicketNotice.setVisibility(isClosedNoticeVisible ? View.VISIBLE : View.GONE);
        sendReplyButton.setVisibility(isClosedNoticeVisible ? View.GONE : View.VISIBLE);

        if (imageCount > 0) {
            manageImagesButton.setColorFilter(Color.GREEN);
        } else {
            manageImagesButton.setColorFilter(Color.RED);
        }
    }

    private void bindReplies(List<TicketReply> replies, boolean canDelete) {
        replyAdapter.setCanDelete(canDelete);
        boolean hasReplies = replies != null && !replies.isEmpty();
        repliesRecyclerView.setVisibility(hasReplies ? View.VISIBLE : View.GONE);
        noRepliesTextView.setVisibility(hasReplies ? View.GONE : View.VISIBLE);

        if (hasReplies) {
            replyAdapter.submitList(replies);
        }
    }

    private void showStatusChangeDialog(List<Status> statuses) {
        final CharSequence[] statusNames = statuses.stream().map(Status::getName).toArray(CharSequence[]::new);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.change_status_title)
                .setItems(statusNames, (dialog, which) -> {
                    Status selectedStatus = statuses.get(which);
                    viewModel.changeStatus(selectedStatus);
                })
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void showDeleteTicketDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_ticket)
                .setMessage(R.string.confirm_delete_ticket)
                .setPositiveButton(R.string.delete_button, (d, w) -> viewModel.deleteTicket())
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void showDeleteReplyDialog(TicketReply reply) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_reply)
                .setMessage(R.string.confirm_delete_reply)
                .setPositiveButton(R.string.delete_button, (d, w) -> viewModel.deleteReply(reply))
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void showErrorState(int messageResId) {
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setText(messageResId);
    }

    private void handleNavigation(TicketViewModel.DetailsNavigation target) {
        if (!isAdded()) return;

        Fragment fragment;
        Bundle args = new Bundle();
        args.putLong("ticketId", ticketId);

        switch (target) {
            case GO_BACK:
                getParentFragmentManager().popBackStack();
                return;
            case GO_TO_EDIT:
                fragment = new TicketFormFragment();
                break;
            case GO_TO_IMAGES:
                fragment = new TicketImageFragment();
                break;
            default:
                return;
        }

        fragment.setArguments(args);
        performTransaction(fragment);
    }

    private void performTransaction(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}