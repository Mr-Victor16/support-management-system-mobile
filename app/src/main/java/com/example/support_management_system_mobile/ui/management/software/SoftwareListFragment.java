package com.example.support_management_system_mobile.ui.management.software;

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
import com.example.support_management_system_mobile.payload.response.SoftwareResponse;
import com.example.support_management_system_mobile.validators.SoftwareValidator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SoftwareListFragment extends Fragment {
    private SoftwareManagementViewModel viewModel;
    private SoftwareManageAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyErrorLayout;
    private TextView emptyErrorTextView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_software_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SoftwareManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadSoftwareList();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.softwareRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyErrorLayout = view.findViewById(R.id.emptyErrorLayout);
        emptyErrorTextView = view.findViewById(R.id.emptyErrorTextView);
        fab = view.findViewById(R.id.fabAddSoftware);

        fab.setOnClickListener(v -> showSoftwareDialog(null));
    }

    private void setupRecyclerView() {
        adapter = new SoftwareManageAdapter(new SoftwareManageAdapter.OnSoftwareInteractionListener() {
            @Override
            public void onItemClick(SoftwareResponse item) {
                showReadOnlyDialog(item);
            }

            @Override
            public void onEdit(SoftwareResponse item) {
                showSoftwareDialog(item);
            }

            @Override
            public void onDelete(SoftwareResponse item) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_software_title)
                        .setMessage(getString(R.string.confirm_delete_software_message, item.name()))
                        .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deleteSoftware(item.softwareID()))
                        .setNegativeButton(R.string.cancel_button, null)
                        .show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.softwareListState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state instanceof SoftwareListUIState.Loading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(state instanceof SoftwareListUIState.Success ? View.VISIBLE : View.GONE);

            boolean isListEmpty = state instanceof SoftwareListUIState.Success && ((SoftwareListUIState.Success) state).softwareList.isEmpty();
            emptyErrorLayout.setVisibility(state instanceof SoftwareListUIState.Error || isListEmpty ? View.VISIBLE : View.GONE);

            if (state instanceof SoftwareListUIState.Success successState) {
                adapter.setCanManage(successState.canManage);
                adapter.submitList(successState.softwareList);
                fab.setVisibility(successState.canManage ? View.VISIBLE : View.GONE);

                if (isListEmpty) {
                    emptyErrorTextView.setText(R.string.no_software_defined);
                }
            } else if (state instanceof SoftwareListUIState.Error) {
                emptyErrorTextView.setText(((SoftwareListUIState.Error) state).message);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showSoftwareDialog(@Nullable SoftwareResponse software) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        boolean isEditing = software != null;
        builder.setTitle(isEditing ? R.string.edit_software_title : R.string.add_new_software_title);

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_software_form, null);
        final TextInputLayout nameInputLayout = dialogView.findViewById(R.id.nameInputLayout);
        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        final TextInputLayout descriptionInputLayout = dialogView.findViewById(R.id.descriptionInputLayout);
        final EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);

        if (isEditing) {
            nameEditText.setText(software.name());
            descriptionEditText.setText(software.description());
        }
        builder.setView(dialogView);

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInputLayout.setError(SoftwareValidator.isNameValid(s.toString()) ? null : getString(R.string.software_name_error));
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        });

        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionInputLayout.setError(SoftwareValidator.isDescriptionValid(s.toString()) ? null : getString(R.string.software_description_error));
            }

            @Override
            public void afterTextChanged(Editable s) {  }
        });

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            boolean isNameValid = SoftwareValidator.isNameValid(name);
            boolean isDescriptionValid = SoftwareValidator.isDescriptionValid(description);

            nameInputLayout.setError(isNameValid ? null : getString(R.string.software_name_error));
            descriptionInputLayout.setError(isDescriptionValid ? null : getString(R.string.software_description_error));

            if (isNameValid && isDescriptionValid) {
                if (isEditing) {
                    viewModel.updateSoftware(software.softwareID(), name, description);
                } else {
                    viewModel.createSoftware(name, description);
                }

                dialog.dismiss();
            }
        });
    }

    private void showReadOnlyDialog(SoftwareResponse item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(item.name())
                .setMessage(item.description())
                .setPositiveButton(R.string.close_button, null)
                .show();
    }
}