package com.example.support_management_system_mobile.ui.management.knowledge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Knowledge;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class KnowledgeListFragment extends Fragment {
    private KnowledgeManagementViewModel viewModel;
    private KnowledgeManageAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyErrorLayout;
    private TextView emptyErrorTextView;
    private FloatingActionButton fab;
    private boolean canManage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(KnowledgeManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadKnowledgeItems();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.knowledgeRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyErrorLayout = view.findViewById(R.id.emptyErrorLayout);
        emptyErrorTextView = view.findViewById(R.id.emptyErrorTextView);
        fab = view.findViewById(R.id.fabAddKnowledge);

        fab.setOnClickListener(v -> navigateToForm(null));
    }

    private void setupRecyclerView() {
        adapter = new KnowledgeManageAdapter(canManage, new KnowledgeManageAdapter.OnKnowledgeInteractionListener() {
            @Override
            public void onEdit(Knowledge item) {
                navigateToForm(item.getId());
            }

            @Override
            public void onDelete(Knowledge item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_knowledge_title)
                        .setMessage(getString(R.string.confirm_delete_knowledge_message, item.getTitle()))
                        .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deleteKnowledgeItem(item.getId()))
                        .setNegativeButton(R.string.cancel_button, null)
                        .show();
            }

            @Override
            public void onItemClick(Knowledge item) {
                if (!canManage) {
                    showReadOnlyDialog(item);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.knowledgeListState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state instanceof KnowledgeListUIState.Loading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(state instanceof KnowledgeListUIState.Success ? View.VISIBLE : View.GONE);

            fab.setVisibility(View.GONE);

            boolean isListEmpty = state instanceof KnowledgeListUIState.Success && ((KnowledgeListUIState.Success) state).knowledgeItems.isEmpty();
            emptyErrorLayout.setVisibility(state instanceof KnowledgeListUIState.Error || isListEmpty ? View.VISIBLE : View.GONE);

            if (state instanceof KnowledgeListUIState.Success) {
                KnowledgeListUIState.Success successState = (KnowledgeListUIState.Success) state;
                adapter.setCanManage(successState.canManage);
                adapter.submitList(successState.knowledgeItems);
                fab.setVisibility(this.canManage ? View.VISIBLE : View.GONE);

                if (isListEmpty) {
                    emptyErrorTextView.setText(R.string.no_knowledge_defined);
                }
            } else if (state instanceof KnowledgeListUIState.Error) {
                emptyErrorTextView.setText(((KnowledgeListUIState.Error) state).message);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToForm(@Nullable Long knowledgeId) {
        KnowledgeFormFragment formFragment = new KnowledgeFormFragment();
        if (knowledgeId != null) {
            Bundle args = new Bundle();
            args.putLong("knowledgeId", knowledgeId);
            formFragment.setArguments(args);
        }

        if (isAdded() && getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, formFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showReadOnlyDialog(Knowledge item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(item.getTitle())
                .setMessage(item.getContent())
                .setPositiveButton(R.string.close_button, null)
                .show();
    }
}