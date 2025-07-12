package com.example.support_management_system_mobile.api;

import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.payload.request.AddTicketReplyRequest;
import com.example.support_management_system_mobile.payload.request.LoginRequest;
import com.example.support_management_system_mobile.payload.request.RegisterRequest;
import com.example.support_management_system_mobile.payload.request.UpdateProfileRequest;
import com.example.support_management_system_mobile.models.Knowledge;
import com.example.support_management_system_mobile.payload.response.LoginResponse;
import com.example.support_management_system_mobile.models.Software;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
}
