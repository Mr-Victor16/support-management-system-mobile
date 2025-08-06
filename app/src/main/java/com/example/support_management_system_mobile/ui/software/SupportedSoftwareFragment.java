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
    private ProgressBar loadingSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supported_software, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SupportedSoftwareViewModel.class);
        setupRecyclerView();
        observeViewModel();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSoftware);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
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
                loadingSpinner.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.GONE);
            } else if (state instanceof SoftwareUIState.Success) {
                loadingSpinner.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                softwareAdapter.submitList(((SoftwareUIState.Success) state).items);
            } else if (state instanceof SoftwareUIState.Empty) {
                loadingSpinner.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                emptyMessage.setVisibility(View.VISIBLE);
                emptyMessage.setText(((SoftwareUIState.Empty) state).message);
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
