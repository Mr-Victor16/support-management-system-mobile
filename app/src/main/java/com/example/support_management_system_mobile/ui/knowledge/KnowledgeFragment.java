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
    private ProgressBar loadingSpinner;
    private TextView emptyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_knowledge, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(KnowledgeViewModel.class);

        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewKnowledge);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        emptyMessage = view.findViewById(R.id.emptyMessage);
    }

    private void setupRecyclerView() {
        knowledgeAdapter = new KnowledgeAdapter();
        knowledgeAdapter.setOnItemClickListener(item -> viewModel.onKnowledgeItemClicked(item.getKnowledge().getId()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(knowledgeAdapter);
    }

    private void observeViewModel() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof KnowledgeUIState.Loading) {
                loadingSpinner.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.GONE);
            } else if (state instanceof KnowledgeUIState.Success) {
                loadingSpinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                emptyMessage.setVisibility(View.GONE);
                knowledgeAdapter.submitList(((KnowledgeUIState.Success) state).items);
            } else if (state instanceof KnowledgeUIState.Empty) {
                loadingSpinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
                emptyMessage.setText(((KnowledgeUIState.Empty) state).message);
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
