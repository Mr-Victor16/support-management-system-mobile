package com.example.support_management_system_mobile.ui.management.category;

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
import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.payload.response.CategoryResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryListFragment extends Fragment {
    private CategoryManagementViewModel viewModel;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private LinearLayout emptyErrorLayout;
    private TextView emptyErrorTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CategoryManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        observeViewModel();

        viewModel.loadCategories();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.categoriesRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyErrorLayout = view.findViewById(R.id.emptyErrorLayout);
        emptyErrorTextView = view.findViewById(R.id.emptyErrorTextView);
        fab = view.findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(v -> showCategoryDialog(null));
    }

    private void setupRecyclerView() {
        AuthContext authContext = viewModel.getAuthContext();

        adapter = new CategoryAdapter(new CategoryAdapter.OnCategoryInteractionListener() {
            @Override
            public void onEdit(CategoryResponse category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDelete(CategoryResponse category) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_category_title)
                        .setMessage(getString(R.string.confirm_delete_category_message, category.getName()))
                        .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deleteCategory(category.getCategoryID()))
                        .setNegativeButton(R.string.cancel_button, null)
                        .show();
            }
        }, authContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.categoryListState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state instanceof CategoryListUIState.Loading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(state instanceof CategoryListUIState.Success ? View.VISIBLE : View.GONE);

            boolean isListEmpty = state instanceof CategoryListUIState.Success && ((CategoryListUIState.Success) state).categories.isEmpty();
            boolean isAdmin = viewModel.getAuthContext().isAdmin();

            emptyErrorLayout.setVisibility(state instanceof CategoryListUIState.Error || isListEmpty ? View.VISIBLE : View.GONE);
            fab.setVisibility(state instanceof CategoryListUIState.Error || isAdmin ? View.VISIBLE : View.GONE);

            if (state instanceof CategoryListUIState.Success) {
                adapter.submitList(((CategoryListUIState.Success) state).categories);
                if (((CategoryListUIState.Success) state).categories.isEmpty()) {
                    emptyErrorTextView.setText(R.string.no_categories_defined);
                }
            } else if (state instanceof CategoryListUIState.Error) {
                emptyErrorTextView.setText(((CategoryListUIState.Error) state).message);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null && isResumed()) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private void showCategoryDialog(@Nullable CategoryResponse category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        boolean isEditing = category != null;
        String title = isEditing ? getString(R.string.edit_category_title) : getString(R.string.add_new_category);
        builder.setTitle(title);

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null);
        final TextInputLayout textInputLayout = dialogView.findViewById(R.id.textInputLayout);
        final EditText input = dialogView.findViewById(R.id.editText);

        if (isEditing) {
            input.setText(category.getName());
        }
        builder.setView(dialogView);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isCategoryNameValid(s.toString())) {
                    textInputLayout.setError(null);
                } else {
                    textInputLayout.setError(getString(R.string.category_name_error));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setPositiveButton(R.string.save, null);
        builder.setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = input.getText().toString().trim();

            if (isCategoryNameValid(name)) {
                if (isEditing) {
                    viewModel.updateCategory(category.getCategoryID(), name);
                } else {
                    viewModel.createCategory(name);
                }
                dialog.dismiss();
            } else {
                textInputLayout.setError(getString(R.string.category_name_error));
            }
        });
    }

    public static boolean isCategoryNameValid(String name) {
        if (name == null) {
            return false;
        }
        String trimmedName = name.trim();
        return trimmedName.length() >= 2 && trimmedName.length() <= 20;
    }
}