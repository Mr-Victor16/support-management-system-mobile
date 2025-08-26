package com.example.support_management_system_mobile.ui.ticket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Image;
import com.example.support_management_system_mobile.models.Ticket;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TicketImageFragment extends Fragment {
    private TicketViewModel viewModel;
    private ImagePagerAdapter pagerAdapter;

    private ViewPager2 viewPager;
    private ProgressBar progressBar;
    private TextView noImagesTextView;
    private ConstraintLayout contentLayout;
    private ImageButton deleteButton;
    private ExtendedFloatingActionButton addButton;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private long ticketId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            ticketId = getArguments().getLong("ticketId", -1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TicketViewModel.class);

        initViews(view);
        setupImagePicker();
        setupListeners();
        observeViewModel();

        Ticket currentTicketInViewModel = viewModel.getCurrentTicket();
        if (currentTicketInViewModel == null || currentTicketInViewModel.getId() != ticketId) {
            viewModel.loadTicketDetails(ticketId);
        }
    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.viewImagePager);
        deleteButton = view.findViewById(R.id.deleteImageButton);
        addButton = view.findViewById(R.id.addImageButton);
        noImagesTextView = view.findViewById(R.id.noImagesTextView);
        progressBar = view.findViewById(R.id.progressBar);
        contentLayout = view.findViewById(R.id.contentLayout);
        pagerAdapter = new ImagePagerAdapter();
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            viewModel.addImageToTicket(imageUri);
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        ImageButton closeButton = getView().findViewById(R.id.closeImagesFragmentButton);
        closeButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        addButton.setOnClickListener(v -> viewModel.onAddImageClicked());
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void observeViewModel() {
        viewModel.ticketDetailsState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state instanceof TicketDetailsUIState.Loading ? View.VISIBLE : View.GONE);
            contentLayout.setVisibility(state instanceof TicketDetailsUIState.Success ? View.VISIBLE : View.GONE);

            if (state instanceof TicketDetailsUIState.Success) {
                TicketDetailsUIState.Success successState = (TicketDetailsUIState.Success) state;

                if (successState.ticket.getId() == ticketId) {
                    boolean hasImages = successState.imageCount > 0;
                    viewPager.setVisibility(hasImages ? View.VISIBLE : View.GONE);
                    noImagesTextView.setVisibility(hasImages ? View.GONE : View.VISIBLE);

                    pagerAdapter.submitList(successState.ticket.getImages());

                    boolean canEdit = successState.controls.canEditImages;
                    deleteButton.setVisibility(hasImages && canEdit ? View.VISIBLE : View.GONE);
                    addButton.setVisibility(canEdit ? View.VISIBLE : View.GONE);
                }
            }
        });

        viewModel.getPickImageEvent().observe(getViewLifecycleOwner(), event -> {
            Boolean trigger = event.getContentIfNotHandled();
            if (trigger != null && trigger) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation() {
        if (pagerAdapter.getCurrentList().isEmpty()) return;

        int currentPosition = viewPager.getCurrentItem();
        Image imageToDelete = pagerAdapter.getCurrentList().get(currentPosition);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_image)
                .setMessage(R.string.confirm_delete_image)
                .setPositiveButton(R.string.delete_button, (dialog, which) -> viewModel.deleteImage(imageToDelete.getId()))
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }
}
