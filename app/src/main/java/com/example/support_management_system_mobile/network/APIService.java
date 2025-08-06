package com.example.support_management_system_mobile.network;

import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.models.Status;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.payload.request.AddTicketReplyRequest;
import com.example.support_management_system_mobile.payload.request.AddTicketRequest;
import com.example.support_management_system_mobile.payload.request.LoginRequest;
import com.example.support_management_system_mobile.payload.request.RegisterRequest;
import com.example.support_management_system_mobile.payload.request.UpdateProfileRequest;
import com.example.support_management_system_mobile.models.Knowledge;
import com.example.support_management_system_mobile.payload.request.UpdateTicketRequest;
import com.example.support_management_system_mobile.payload.response.LoginResponse;
import com.example.support_management_system_mobile.models.Software;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<String> register(@Body RegisterRequest request);

    @GET("/api/software")
    Call<List<Software>> getSupportedSoftwareList();

    @GET("/api/knowledge-bases")
    Call<List<Knowledge>> getKnowledgeItems();

    @PUT("/api/profiles")
    Call<Void> updateProfile(@Header("Authorization") String bearerToken, @Body UpdateProfileRequest request);

    @GET("/api/tickets/user")
    Call<List<Ticket>> getUserTickets(@Header("Authorization") String bearerToken);

    @GET("/api/tickets")
    Call<List<Ticket>> getAllTickets(@Header("Authorization") String bearerToken);

    @GET("/api/tickets/{ticketID}")
    Call<Ticket> getTicketById(@Path("ticketID") Long ticketID, @Header("Authorization") String bearerToken);

    @DELETE("api/tickets/reply/{replyID}")
    Call<Void> deleteReply(@Path("replyID") Long replyID, @Header("Authorization") String bearerToken);

    @DELETE("api/tickets/{ticketID}")
    Call<Void> deleteTicket(@Path("ticketID") Long ticketID, @Header("Authorization") String bearerToken);

    @POST("api/tickets/reply")
    Call<Void> addTicketReply(@Body AddTicketReplyRequest request, @Header("Authorization") String bearerToken);

    @Multipart
    @POST("api/tickets/{ticketID}/image")
    Call<Void> uploadTicketImages(
            @Path("ticketID") Long ticketId,
            @Part List<MultipartBody.Part> files,
            @Header("Authorization") String bearerToken
    );

    @DELETE("api/tickets/image/{imageID}")
    Call<Void> deleteTicketImage(@Path("imageID") Long imageId, @Header("Authorization") String bearerToken);

    @GET("api/categories")
    Call<List<Category>> getAllCategories(@Header("Authorization") String bearerToken);

    @GET("api/software")
    Call<List<Software>> getAllSoftware(@Header("Authorization") String bearerToken);

    @POST("api/tickets")
    Call<Void> createTicket(@Body AddTicketRequest request, @Header("Authorization") String bearerToken);

    @PUT("api/tickets")
    Call<Void> updateTicket(@Body UpdateTicketRequest request, @Header("Authorization") String bearerToken);

    @GET("api/statuses")
    Call<List<Status>> getAllStatuses(@Header("Authorization") String bearerToken);

    @GET("api/priorities")
    Call<List<Priority>> getAllPriorities(@Header("Authorization") String bearerToken);
}
