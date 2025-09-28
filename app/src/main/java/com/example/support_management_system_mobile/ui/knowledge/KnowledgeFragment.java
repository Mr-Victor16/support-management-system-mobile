package com.example.support_management_system_mobile.ui.knowledge;

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

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class KnowledgeFragment extends Fragment {
    private KnowledgeViewModel viewModel;
    private KnowledgeAdapter knowledgeAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        viewModel = new ViewModelProvider(this).get(KnowledgeViewModel.class);
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewKnowledge);
        progressBar = view.findViewById(R.id.progressBar);
        emptyMessage = view.findViewById(R.id.emptyMessage);
    }

    private void setupRecyclerView() {
        knowledgeAdapter = new KnowledgeAdapter();
        knowledgeAdapter.setOnItemClickListener(item -> viewModel.onKnowledgeItemClicked(item.knowledge().id()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(knowledgeAdapter);
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof KnowledgeUIState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.GONE);
            } else if (state instanceof KnowledgeUIState.Success successState) {
                progressBar.setVisibility(View.GONE);
                knowledgeAdapter.submitList(successState.items);

                if (successState.items.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);
                    emptyMessage.setText(R.string.no_data_to_display);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.GONE);
                }
            } else if (state instanceof KnowledgeUIState.Error errorState) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
                emptyMessage.setText(errorState.message);
            }
        });

        viewModel.toastEvent.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
