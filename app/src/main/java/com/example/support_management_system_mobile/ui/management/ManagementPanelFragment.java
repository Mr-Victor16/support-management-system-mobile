package com.example.support_management_system_mobile.ui.management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.databinding.FragmentManagementPanelBinding;
import com.example.support_management_system_mobile.ui.management.category.CategoryListFragment;
import com.example.support_management_system_mobile.ui.management.knowledge.KnowledgeListFragment;
import com.example.support_management_system_mobile.ui.management.priority.PriorityListFragment;
import com.example.support_management_system_mobile.ui.management.software.SoftwareListFragment;
import com.example.support_management_system_mobile.ui.management.status.StatusListFragment;
import com.example.support_management_system_mobile.ui.management.user.UserListFragment;

public class ManagementPanelFragment extends Fragment {
    private FragmentManagementPanelBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentManagementPanelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.manageUsersCard.setOnClickListener(v -> navigateTo(new UserListFragment()));
        binding.manageCategoriesCard.setOnClickListener(v -> navigateTo(new CategoryListFragment()));
        binding.manageKnowledgeCard.setOnClickListener(v -> navigateTo(new KnowledgeListFragment()));
        binding.managePrioritiesCard.setOnClickListener(v -> navigateTo(new PriorityListFragment()));
        binding.manageSoftwareCard.setOnClickListener(v -> navigateTo(new SoftwareListFragment()));
        binding.manageStatusesCard.setOnClickListener(v -> navigateTo(new StatusListFragment()));
    }

    private void navigateTo(Fragment destinationFragment) {
        if (isAdded() && getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, destinationFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}