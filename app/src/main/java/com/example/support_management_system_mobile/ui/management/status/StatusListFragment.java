package com.example.support_management_system_mobile.ui.management.status;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.payload.response.StatusResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatusListFragment extends Fragment {
    private StatusManagementViewModel viewModel;
    private StatusAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyErrorLayout;
    private TextView emptyErrorTextView;
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StatusManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadStatusList();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.statusRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyErrorLayout = view.findViewById(R.id.emptyErrorLayout);
        emptyErrorTextView = view.findViewById(R.id.emptyErrorTextView);
        fab = view.findViewById(R.id.fabAddStatus);

        fab.setOnClickListener(v -> showStatusDialog(null));
    }

    private void setupRecyclerView() {
        adapter = new StatusAdapter(new StatusAdapter.OnStatusInteractionListener() {
            @Override
            public void onEdit(StatusResponse item) {
                showStatusDialog(item);
            }

            @Override
            public void onDelete(StatusResponse item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_status_title)
                        .setMessage(getString(R.string.confirm_delete_status_message, item.name()))
                        .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deleteStatus(item.statusID()))
                        .setNegativeButton(R.string.cancel_button, null)
                        .show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.statusListState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state instanceof StatusListUIState.Loading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(state instanceof StatusListUIState.Success ? View.VISIBLE : View.GONE);

            boolean isListEmpty = state instanceof StatusListUIState.Success && ((StatusListUIState.Success) state).statusList.isEmpty();
            emptyErrorLayout.setVisibility(state instanceof StatusListUIState.Error || isListEmpty ? View.VISIBLE : View.GONE);

            if (state instanceof StatusListUIState.Success successState) {
                adapter.setCanManage(successState.canManage);
                adapter.submitList(successState.statusList);
                fab.setVisibility(successState.canManage ? View.VISIBLE : View.GONE);

                if (isListEmpty) {
                    emptyErrorTextView.setText(R.string.no_status_defined);
                }
            } else if (state instanceof StatusListUIState.Error) {
                emptyErrorTextView.setText(((StatusListUIState.Error) state).message);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showStatusDialog(@Nullable StatusResponse status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        boolean isEditing = status != null;
        builder.setTitle(isEditing ? R.string.edit_status_title : R.string.add_new_status_title);

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_status_form, null);
        final TextInputLayout nameInputLayout = dialogView.findViewById(R.id.nameInputLayout);
        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        final SwitchMaterial closesTicketSwitch = dialogView.findViewById(R.id.closesTicketSwitch);
        final SwitchMaterial defaultStatusSwitch = dialogView.findViewById(R.id.defaultStatusSwitch);

        if (isEditing) {
            nameEditText.setText(status.name());
            closesTicketSwitch.setChecked(status.closeTicket());
            defaultStatusSwitch.setChecked(status.defaultStatus());
        }
        builder.setView(dialogView);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setError(isNameValid(s.toString()) ? null : getString(R.string.status_name_error));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            boolean closesTicket = closesTicketSwitch.isChecked();
            boolean isDefault = defaultStatusSwitch.isChecked();

            boolean isNameValid = isNameValid(name);
            nameInputLayout.setError(isNameValid ? null : getString(R.string.status_name_error));

            if (isNameValid) {
                if (isEditing) {
                    viewModel.updateStatus(status.statusID(), name, closesTicket, isDefault);
                } else {
                    viewModel.createStatus(name, closesTicket, isDefault);
                }
                dialog.dismiss();
            }
        });
    }

    public boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 2 && name.length() <= 20;
    }
}