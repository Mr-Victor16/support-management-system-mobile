package com.example.support_management_system_mobile.ui.management.category;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.auth.AuthContext;
import com.example.support_management_system_mobile.data.repository.CategoryRepository;
import com.example.support_management_system_mobile.models.Event;
import com.example.support_management_system_mobile.payload.response.CategoryResponse;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class CategoryManagementViewModel extends ViewModel {
    private final CategoryRepository categoryRepository;
    private final Application application;
    private final AuthContext authContext;

    private final MutableLiveData<CategoryListUIState> _categoryListState = new MutableLiveData<>();
    public final LiveData<CategoryListUIState> categoryListState = _categoryListState;

    private final MutableLiveData<Event<String>> _toastMessage = new MutableLiveData<>();
    public final LiveData<Event<String>> toastMessage = _toastMessage;

    @Inject
    public CategoryManagementViewModel(Application application, CategoryRepository categoryRepository, AuthContext authContext) {
        this.application = application;
        this.categoryRepository = categoryRepository;
        this.authContext = authContext;
    }

    public void loadCategories() {
        _categoryListState.setValue(new CategoryListUIState.Loading());

        categoryRepository.getCategoriesWithUseNumber(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _categoryListState.postValue(new CategoryListUIState.Success(response.body()));
                } else {
                    _categoryListState.postValue(new CategoryListUIState.Error(application.getString(R.string.error_loading_data)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable t) {
                _categoryListState.postValue(new CategoryListUIState.Error(application.getString(R.string.server_error)));
            }
        });
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteCategory(categoryId, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.category_deleted_successfully)));
                    loadCategories();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.category_delete_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void createCategory(String name) {
        categoryRepository.createCategory(name, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.category_added_successfully)));
                    loadCategories();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.category_add_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public void updateCategory(Long categoryId, String newName) {
        categoryRepository.updateCategory(categoryId, newName, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.category_updated_successfully)));
                    loadCategories();
                } else {
                    _toastMessage.postValue(new Event<>(application.getString(R.string.category_update_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _toastMessage.postValue(new Event<>(application.getString(R.string.server_error)));
            }
        });
    }

    public AuthContext getAuthContext() {
        return authContext;
    }
}
