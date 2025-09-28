package com.example.support_management_system_mobile.ui.management.knowledge;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.data.models.Software;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class KnowledgeFormFragment extends Fragment {
    private KnowledgeManagementViewModel viewModel;

    private ProgressBar loadingProgressBar, submittingProgressBar;
    private ScrollView formContent;
    private TextView errorTextView;
    private TextView formHeader;
    private TextInputLayout titleInputLayout, contentInputLayout;
    private EditText editTitle, editContent;
    private AutoCompleteTextView softwareAutoComplete;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(KnowledgeManagementViewModel.class);

        Long knowledgeId = getArguments() != null ? getArguments().getLong("knowledgeId", -1L) : -1L;
        if (knowledgeId == -1L) knowledgeId = null;

        initViews(view);

        int headerResId = (knowledgeId == null) ? R.string.add_new_knowledge : R.string.edit_knowledge;
        formHeader.setText(headerResId);

        setupListeners();
        observeViewModel();

        viewModel.loadKnowledgeForm(knowledgeId);
    }

    private void initViews(View view) {
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        submittingProgressBar = view.findViewById(R.id.submittingProgressBar);
        formContent = view.findViewById(R.id.formContent);
        errorTextView = view.findViewById(R.id.errorTextView);
        formHeader = view.findViewById(R.id.formHeader);
        titleInputLayout = view.findViewById(R.id.titleInputLayout);
        editTitle = view.findViewById(R.id.editTitle);
        contentInputLayout = view.findViewById(R.id.contentInputLayout);
        editContent = view.findViewById(R.id.editContent);
        softwareAutoComplete = view.findViewById(R.id.softwareAutoComplete);
        saveButton = view.findViewById(R.id.saveButton);
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> viewModel.saveKnowledgeItem());

        editTitle.addTextChangedListener(new SimpleTextWatcher(s ->
                viewModel.onFieldChanged(KnowledgeManagementViewModel.FormField.TITLE, s)));
        editContent.addTextChangedListener(new SimpleTextWatcher(s ->
                viewModel.onFieldChanged(KnowledgeManagementViewModel.FormField.CONTENT, s)));

        softwareAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Software selectedSoftware = (Software) parent.getItemAtPosition(position);
            viewModel.selectedSoftware.setValue(selectedSoftware);
        });

        softwareAutoComplete.addTextChangedListener(new SimpleTextWatcher(s -> {
            if (s.isEmpty()) {
                viewModel.selectedSoftware.setValue(null);
            }
        }));
    }

    private void observeViewModel() {
        viewModel.knowledgeFormState.observe(getViewLifecycleOwner(), state -> {
            loadingProgressBar.setVisibility(View.GONE);
            formContent.setVisibility(View.GONE);
            errorTextView.setVisibility(View.GONE);
            submittingProgressBar.setVisibility(View.GONE);

            if (state instanceof KnowledgeFormUIState.Loading) {
                loadingProgressBar.setVisibility(View.VISIBLE);
            }
            else if (state instanceof KnowledgeFormUIState.Error) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(((KnowledgeFormUIState.Error) state).message);
            }
            else if (state instanceof KnowledgeFormUIState.Submitting) {
                formContent.setVisibility(View.VISIBLE);
                setFormEnabled(false);
                submittingProgressBar.setVisibility(View.VISIBLE);
            }
            else if (state instanceof KnowledgeFormUIState.Editing) {
                formContent.setVisibility(View.VISIBLE);
                setFormEnabled(true);
                saveButton.setText(((KnowledgeFormUIState.Editing) state).saveButtonTextResId);
            }
            else if (state instanceof KnowledgeFormUIState.Success) {
                getParentFragmentManager().popBackStack();
            }
        });

        viewModel.titleError.observe(getViewLifecycleOwner(), errorResId ->
                titleInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.contentError.observe(getViewLifecycleOwner(), errorResId ->
                contentInputLayout.setError(errorResId != null ? getString(errorResId) : null));

        viewModel.isSaveButtonEnabled.observe(getViewLifecycleOwner(), isEnabled ->
                saveButton.setEnabled(isEnabled != null && isEnabled));

        viewModel.softwareList.observe(getViewLifecycleOwner(), softwareList -> {
            if (getContext() == null || softwareList == null) return;

            ArrayAdapter<Software> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, softwareList);
            softwareAutoComplete.setAdapter(adapter);
        });

        viewModel.knowledgeTitle.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editTitle.getText().toString(), text)) editTitle.setText(text);
        });
        viewModel.knowledgeContent.observe(getViewLifecycleOwner(), text -> {
            if (!Objects.equals(editContent.getText().toString(), text)) editContent.setText(text);
        });
        viewModel.selectedSoftware.observe(getViewLifecycleOwner(), this::setAutocompleteSelection);

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setFormEnabled(boolean isEnabled) {
        titleInputLayout.setEnabled(isEnabled);
        contentInputLayout.setEnabled(isEnabled);
        softwareAutoComplete.setEnabled(isEnabled);
        saveButton.setEnabled(isEnabled);
    }

    private void setAutocompleteSelection(Software value) {
        String newText = (value != null) ? value.toString() : "";

        if (!softwareAutoComplete.getText().toString().equals(newText)) {
            softwareAutoComplete.setText(newText, false);
        }
    }

    private record SimpleTextWatcher(java.util.function.Consumer<String> onTextChanged) implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onTextChanged.accept(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {  }
    }
}