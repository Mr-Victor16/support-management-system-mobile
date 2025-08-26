package com.example.support_management_system_mobile.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.support_management_system_mobile.ui.MainActivity;
import com.example.support_management_system_mobile.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final AuthContext authContext;
    private final Context context;

    public AuthInterceptor(Context context, AuthContext authContext) {
        this.context = context;
        this.authContext = authContext;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        String token = authContext.getAuthToken();
        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }

        Request newRequest = requestBuilder.build();
        Response response = chain.proceed(newRequest);

        if (response.code() == 401) {
            String responseBodyString = response.peekBody(Long.MAX_VALUE).string();
            try {
                JSONObject responseJson = new JSONObject(responseBodyString);
                String message = responseJson.optString("message", "");

                if (message.contains("JWT token has expired")) {
                    authContext.logout();

                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, R.string.session_expired, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    });
                }
            } catch (Exception e) {
                Log.e("AuthInterceptor", "Failed to parse 401 response body as JSON", e);
            }
        }

        return response;
    }
}
