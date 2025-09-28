package com.example.support_management_system_mobile.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FilePreparer {
    private final Context appContext;

    @Inject
    public FilePreparer(@ApplicationContext Context appContext) {
        this.appContext = appContext;
    }

    @Nullable
    public MultipartBody.Part prepareImagePart(String partName, Uri imageUri) {
        try (InputStream inputStream = appContext.getContentResolver().openInputStream(imageUri)) {
            if (inputStream == null) {
                return null;
            }

            byte[] fileBytes = inputStreamToByteArray(inputStream);
            String mimeType = appContext.getContentResolver().getType(imageUri);

            if (mimeType == null) mimeType = "application/octet-stream";
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);

            return MultipartBody.Part.createFormData(partName, "image.jpg", requestFile);
        } catch (IOException e) {
            Log.e("FilePreparer", "Failed to prepare image part", e);
            return null;
        }
    }

    public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}
