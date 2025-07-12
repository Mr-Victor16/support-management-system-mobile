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
import com.example.support_management_system_mobile.adapter.KnowledgeAdapter;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.models.Knowledge;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowledgeFragment extends Fragment {
    private TextView emptyMessage;
    private ProgressBar loadingSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_knowledge, container, false);

        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        emptyMessage = view.findViewById(R.id.emptyMessage);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewKnowledge);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadKnowledge(recyclerView);

        return view;
    }

    private void loadKnowledge(RecyclerView recyclerView) {
        loadingSpinner.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.GONE);

        APIClient.getAPIService(requireContext()).getKnowledgeItems().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Knowledge>> call, Response<List<Knowledge>> response) {
                loadingSpinner.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    KnowledgeAdapter adapter = new KnowledgeAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    emptyMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Knowledge>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                loadingSpinner.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
            }
        });
    }
}