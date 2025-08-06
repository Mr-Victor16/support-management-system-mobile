package com.example.support_management_system_mobile.ui.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.models.User;
import com.example.support_management_system_mobile.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketListFragment extends Fragment {
    private TicketAdapter adapter;
    private final List<Ticket> tickets = new ArrayList<>();

    private TextView ticketListHeader, noTicketsTextView;
    private FloatingActionButton addTicketButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (JWTUtils.getToken(requireContext()) == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return null;
        }

        View view =  inflater.inflate(R.layout.fragment_ticket_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.ticketRecyclerView);
        adapter = new TicketAdapter(tickets, this::onTicketClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        addTicketButton = view.findViewById(R.id.addTicketButton);
        addTicketButton.setOnClickListener(v -> navigateToAddTicket());

        noTicketsTextView = view.findViewById(R.id.noTicketsTextView);
        ticketListHeader = view.findViewById(R.id.ticketsHeader);
        loadTickets();

        return view;
    }

    private void onTicketClick(Ticket ticket, Boolean newTicket) {
        User user = JWTUtils.getCurrentUser(getContext());
        Fragment fragment = TicketDetailsFragment.newInstance(ticket, user, newTicket);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToAddTicket() {
        Intent intent = new Intent(getContext(), TicketFormActivity.class);
        //startActivity(intent);
        addTicketLauncher.launch(intent);
    }

    private void loadTickets() {
        if(JWTUtils.getUserRole(getContext()).equals("ROLE_USER")) {
            ticketListHeader.setText(R.string.my_tickets_header);
            loadUserTickets();
        } else {
            ticketListHeader.setText(R.string.all_tickets_header);
            addTicketButton.setVisibility(View.GONE);
            loadAllTickets();
        }
    }

    private void loadAllTickets() {
        String token = "Bearer " + JWTUtils.getToken(requireContext());

        APIClient.getAPIService(requireContext()).getAllTickets(token).enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ticket>> call, @NonNull Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tickets.clear();
                    tickets.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    noTicketsTextView.setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Ticket>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserTickets() {
        String token = "Bearer " + JWTUtils.getToken(requireContext());

        APIClient.getAPIService(requireContext()).getUserTickets(token).enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ticket>> call, @NonNull Response<List<Ticket>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tickets.clear();
                    tickets.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    noTicketsTextView.setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Ticket>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<Intent> addTicketLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    boolean ticketAdded = result.getData().getBooleanExtra("ticket_added", false);
                    if (ticketAdded) {
                        Ticket ticket = (Ticket) result.getData().getSerializableExtra("ticket_object");
                        onTicketClick(ticket, true);
                    }
                }
            });
}