package com.example.support_management_system_mobile.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.adapter.SoftwareAdapter;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.models.Software;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportedSoftwareFragment extends Fragment {
    private TextView emptyMessage;
    private ProgressBar loadingSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_software, container, false);

        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        emptyMessage = view.findViewById(R.id.emptyMessage);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSoftware);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSupportedSoftware(recyclerView);

        return view;
    }

    private void loadSupportedSoftware(RecyclerView recyclerView) {
        loadingSpinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.GONE);

        APIClient.getAPIService(requireContext()).getSupportedSoftwareList().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Software>> call, Response<List<Software>> response) {
                loadingSpinner.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    SoftwareAdapter adapter = new SoftwareAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    emptyMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Software>> call, Throwable t) {
                Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}