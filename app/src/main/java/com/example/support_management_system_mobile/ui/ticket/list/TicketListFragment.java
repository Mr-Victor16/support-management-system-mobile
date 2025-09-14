package com.example.support_management_system_mobile.ui.ticket.list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.ticket.details.TicketDetailsFragment;
import com.example.support_management_system_mobile.ui.ticket.form.TicketFormFragment;
import com.example.support_management_system_mobile.ui.ticket.TicketViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TicketListFragment extends Fragment {
    private TicketViewModel viewModel;
    private TicketAdapter ticketAdapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyListMessage;
    private TextView headerTextView;
    private FloatingActionButton addTicketButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.ticketRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyListMessage = view.findViewById(R.id.noTicketsTextView);
        headerTextView = view.findViewById(R.id.ticketsHeader);
        addTicketButton = view.findViewById(R.id.addTicketButton);
    }

    private void setupRecyclerView() {
        ticketAdapter = new TicketAdapter(ticket -> navigateToDetails(ticket.getId()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(ticketAdapter);
    }

    private void setupClickListeners() {
        addTicketButton.setOnClickListener(v -> performTransaction(new TicketFormFragment()));
    }

    private void observeViewModel() {
        viewModel.ticketListState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyListMessage.setVisibility(View.GONE);
            addTicketButton.setVisibility(View.GONE);

            if (state instanceof TicketListUIState.Loading loadingState) {
                headerTextView.setText(loadingState.headerTextResId);
                progressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof TicketListUIState.Success successState) {
                headerTextView.setText(successState.headerTextResId);
                ticketAdapter.submitList(successState.tickets);
                recyclerView.setVisibility(View.VISIBLE);

                if (successState.canAddTicket) {
                    addTicketButton.setVisibility(View.VISIBLE);
                }

                if (successState.tickets.isEmpty()) {
                    emptyListMessage.setText(R.string.no_tickets_found);
                    emptyListMessage.setVisibility(View.VISIBLE);
                } else {
                    emptyListMessage.setVisibility(View.GONE);
                }
            } else if (state instanceof TicketListUIState.Error errorState) {
                headerTextView.setText(errorState.headerTextResId);
                emptyListMessage.setText(errorState.message);
                emptyListMessage.setVisibility(View.VISIBLE);
            } else if (state instanceof TicketListUIState.AccessDenied deniedState) {
                headerTextView.setText(deniedState.headerTextResId);
                emptyListMessage.setText(deniedState.message);
                emptyListMessage.setVisibility(View.VISIBLE);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDetails(long ticketId) {
        TicketDetailsFragment detailsFragment = new TicketDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("ticketId", ticketId);
        detailsFragment.setArguments(args);

        performTransaction(detailsFragment);
    }

    private void performTransaction(Fragment fragment) {
        if (isAdded() && getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewModel != null) {
            viewModel.loadTickets();
        }
    }
}