package com.example.support_management_system_mobile.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.adapter.ImagePagerAdapter;
import com.example.support_management_system_mobile.auth.APIClient;
import com.example.support_management_system_mobile.auth.JWTUtils;
import com.example.support_management_system_mobile.models.Image;
import com.example.support_management_system_mobile.models.Ticket;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketImageActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_POSITION = "extra_position";
    public static final String TICKET_ID = "ticket_id";

    private List<Image> images = new ArrayList<>();
    private ImagePagerAdapter adapter;
    private FloatingActionButton addImageButton;
    private ViewPager2 viewPager;
    private Long ticketId;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket_image);

        applyWindowInsets();
        setupImagePicker();
        extractIntentData();
        initUI();
        setupListeners();

        if(images.isEmpty()) launchImagePicker();
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewImagePager), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        handleImagePicked(imageUri);
                    }
                }
        );
    }

    private void extractIntentData() {
        images = (List<Image>) getIntent().getSerializableExtra(EXTRA_IMAGES);
        ticketId = getIntent().getLongExtra(TICKET_ID, 0);
    }

    private void initUI() {
        int pos = getIntent().getIntExtra(EXTRA_POSITION, 0);

        viewPager = findViewById(R.id.viewImagePager);
        adapter = new ImagePagerAdapter(images, this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos, false);

        addImageButton = findViewById(R.id.addImageButton);
    }

    private void setupListeners() {
        ImageButton deleteButton = findViewById(R.id.deleteImageButton);
        ImageButton closeButton = findViewById(R.id.closeImagesActivityButton);

        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
        closeButton.setOnClickListener(v -> finish());
        addImageButton.setOnClickListener(v -> launchImagePicker());
    }

    private void showDeleteConfirmation() {
        int position = viewPager.getCurrentItem();
        Image currentImage = images.get(position);

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_image)
                .setMessage(R.string.confirm_delete_image)
                .setPositiveButton(R.string.delete_button, (dialog, which) -> deleteImage(currentImage.getId(), position))
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void handleImagePicked(Uri imageUri) {
        try {
            File file = getFileFromUri(this, imageUri);
            MultipartBody.Part imagePart = createMultipartFromFile(file);
            uploadImageToTicket(ticketId, imagePart);
        } catch (IOException e) {
            showToast(R.string.file_processing_error);
        }
    }

    private File getFileFromUri(Context context, Uri uri) throws IOException {
        String fileName = "image_" + System.currentTimeMillis();
        File file = new File(context.getCacheDir(), fileName);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    private MultipartBody.Part createMultipartFromFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("files", file.getName(), requestFile);
    }

    private void uploadImageToTicket(Long ticketId, MultipartBody.Part imagePart) {
        String token = "Bearer " + JWTUtils.getToken(this);

        APIClient.getAPIService(this).uploadTicketImages(ticketId, Collections.singletonList(imagePart), token)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showToast(R.string.image_added);
                            setResult(Activity.RESULT_OK);
                            fetchUpdatedTicket();
                        } else {
                            showToast(R.string.server_error);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast(R.string.server_error);
                    }
                });
    }

    private void deleteImage(Long imageId, int position) {
        String token = JWTUtils.getToken(this);

        APIClient.getAPIService(this).deleteTicketImage(imageId, "Bearer " + token)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            images.remove(position);
                            adapter.notifyItemRemoved(position);
                            showToast(R.string.image_deleted);
                            setResult(Activity.RESULT_OK);

                            if (images.isEmpty()) {
                                finish();
                            }
                        } else {
                            showToast(R.string.server_error);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        showToast(R.string.server_error);
                    }
                });
    }

    private void fetchUpdatedTicket() {
        String token = "Bearer " + JWTUtils.getToken(this);

        APIClient.getAPIService(this).getTicketById(ticketId, token).enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateImages(response.body().getImages());
                } else {
                    showToast(R.string.server_error);
                }
            }

            @Override
            public void onFailure(Call<Ticket> call, Throwable t) {
                showToast(R.string.server_error);
            }
        });
    }

    private void showToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }
}