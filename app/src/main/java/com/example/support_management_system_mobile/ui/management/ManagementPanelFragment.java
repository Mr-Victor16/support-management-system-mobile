package com.example.support_management_system_mobile.ui.management;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.ui.management.category.CategoryListFragment;
import com.example.support_management_system_mobile.ui.management.knowledge.KnowledgeListFragment;
import com.example.support_management_system_mobile.ui.management.priority.PriorityListFragment;
import com.example.support_management_system_mobile.ui.management.software.SoftwareListFragment;
import com.example.support_management_system_mobile.ui.management.status.StatusListFragment;
import com.example.support_management_system_mobile.ui.management.user.UserListFragment;

public class ManagementPanelFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_management_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.manageCategoriesButton).setOnClickListener(v -> navigateTo(new CategoryListFragment()));

        view.findViewById(R.id.manageKnowledgeButton).setOnClickListener(v -> navigateTo(new KnowledgeListFragment()));

        view.findViewById(R.id.managePrioritiesButton).setOnClickListener(v -> navigateTo(new PriorityListFragment()));

        view.findViewById(R.id.manageSoftwareButton).setOnClickListener(v -> navigateTo(new SoftwareListFragment()));

        view.findViewById(R.id.manageStatusesButton).setOnClickListener(v -> navigateTo(new StatusListFragment()));

        view.findViewById(R.id.manageUsersButton).setOnClickListener(v -> navigateTo(new UserListFragment()));
    }

    private void navigateTo(Fragment destinationFragment) {
        if (isAdded() && getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.mainContainer, destinationFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}