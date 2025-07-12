package com.example.support_management_system_mobile.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.support_management_system_mobile.MainActivity;
import com.example.support_management_system_mobile.R;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        String token = JWTUtils.getToken(context);
        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        Response response = chain.proceed(request);

        if (response.code() == 401 && response.message().contains("expired")) {
            JWTUtils.clearData(context);

            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, R.string.session_expired, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            });

            throw new IOException(String.valueOf(R.string.session_expired));
        }

        return response;
    }
}
