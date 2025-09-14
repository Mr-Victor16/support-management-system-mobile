package com.example.support_management_system_mobile.ui.software;

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
public class SupportedSoftwareFragment extends Fragment {
    private SupportedSoftwareViewModel viewModel;
    private SoftwareAdapter softwareAdapter;
    private RecyclerView recyclerView;
    private TextView emptyMessage;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supported_software, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        viewModel = new ViewModelProvider(this).get(SupportedSoftwareViewModel.class);
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSoftware);
        progressBar = view.findViewById(R.id.progressBar);
        emptyMessage = view.findViewById(R.id.emptyMessage);
    }

    private void setupRecyclerView() {
        softwareAdapter = new SoftwareAdapter();
        softwareAdapter.setOnItemClickListener(item -> viewModel.onSoftwareItemClicked(item));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(softwareAdapter);
    }

    private void observeViewModel() {
        viewModel.screenState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof SoftwareUIState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.GONE);
            } else if (state instanceof SoftwareUIState.Success successState) {
                progressBar.setVisibility(View.GONE);
                softwareAdapter.submitList(successState.items);

                if (successState.items.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);
                    emptyMessage.setText(R.string.no_data_to_display);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyMessage.setVisibility(View.GONE);
                }
            } else if (state instanceof SoftwareUIState.Error errorState) {
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
