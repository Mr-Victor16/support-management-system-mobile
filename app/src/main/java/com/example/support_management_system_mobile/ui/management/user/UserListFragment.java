package com.example.support_management_system_mobile.ui.management.user;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.utils.AuthContext;
import com.example.support_management_system_mobile.data.payload.response.UserDetailsResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserListFragment extends Fragment {
    private UserManagementViewModel viewModel;
    private UserAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyErrorTextView;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadUserList();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.userRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyErrorTextView = view.findViewById(R.id.emptyErrorTextView);
        fab = view.findViewById(R.id.fabAddUser);

        fab.setOnClickListener(v -> navigateToForm(null));
    }

    private void setupRecyclerView() {
        AuthContext authContext = viewModel.getAuthContext();

        adapter = new UserAdapter(new UserAdapter.OnUserInteractionListener() {
            @Override
            public void onEdit(UserDetailsResponse item) {
                navigateToForm(item.id());
            }

            @Override
            public void onDelete(UserDetailsResponse item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_user_title)
                        .setMessage(getString(R.string.confirm_delete_user_message, item.username()))
                        .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deleteUser(item.id()))
                        .setNegativeButton(R.string.cancel_button, null)
                        .show();
            }
        }, authContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.userListState.observe(getViewLifecycleOwner(), state -> {
            if (state instanceof UserListUIState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyErrorTextView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            } else if (state instanceof UserListUIState.Success successState) {
                progressBar.setVisibility(View.GONE);
                adapter.setCanManage(successState.canManage);
                adapter.submitList(successState.userList);
                fab.setVisibility(View.VISIBLE);

                if (successState.userList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyErrorTextView.setText(R.string.server_error);
                    emptyErrorTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyErrorTextView.setVisibility(View.GONE);
                }
            } else if (state instanceof UserListUIState.Error errorState) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                emptyErrorTextView.setText(errorState.message);
                emptyErrorTextView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateToForm(@Nullable Long userId) {
        UserFormFragment formFragment = new UserFormFragment();
        if (userId != null) {
            Bundle args = new Bundle();
            args.putLong("userId", userId);
            formFragment.setArguments(args);
        }

        if (isAdded() && getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, formFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}