package com.example.support_management_system_mobile.network;

import com.example.support_management_system_mobile.models.Category;
import com.example.support_management_system_mobile.models.Priority;
import com.example.support_management_system_mobile.models.Status;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.payload.request.add.AddKnowledgeRequest;
import com.example.support_management_system_mobile.payload.request.add.AddSoftwareRequest;
import com.example.support_management_system_mobile.payload.request.add.AddStatusRequest;
import com.example.support_management_system_mobile.payload.request.add.AddTicketReplyRequest;
import com.example.support_management_system_mobile.payload.request.add.AddTicketRequest;
import com.example.support_management_system_mobile.payload.request.add.AddCategoryRequest;
import com.example.support_management_system_mobile.payload.request.add.AddPriorityRequest;
import com.example.support_management_system_mobile.payload.request.LoginRequest;
import com.example.support_management_system_mobile.payload.request.RegisterRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateCategoryRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateKnowledgeRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdatePriorityRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateProfileRequest;
import com.example.support_management_system_mobile.models.Knowledge;
import com.example.support_management_system_mobile.payload.request.update.UpdateSoftwareRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateStatusRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateTicketRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateTicketStatusRequest;
import com.example.support_management_system_mobile.payload.response.CategoryResponse;
import com.example.support_management_system_mobile.payload.response.LoginResponse;
import com.example.support_management_system_mobile.models.Software;
import com.example.support_management_system_mobile.payload.response.PriorityResponse;
import com.example.support_management_system_mobile.payload.response.SoftwareResponse;
import com.example.support_management_system_mobile.payload.response.StatusResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    // AUTH
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<String> register(@Body RegisterRequest request);


    // SOFTWARE
    @GET("/api/software")
    Call<List<Software>> getSoftwareList();

    @GET("/api/software/use")
    Call<List<SoftwareResponse>> getSoftwareListWithUseNumber();

    @PUT("/api/software")
    Call<Void> updateSoftware(@Body UpdateSoftwareRequest request);

    @POST("/api/software")
    Call<Void> addSoftware(@Body AddSoftwareRequest request);

    @DELETE("/api/software/{softwareID}")
    Call<Void> deleteSoftware(@Path("softwareID") Long softwareID);


    // KNOWLEDGE
    @GET("/api/knowledge-bases")
    Call<List<Knowledge>> getKnowledgeItems();

    @GET("/api/knowledge-bases/{knowledgeID}")
    Call<Knowledge> getKnowledgeItemById(@Path("knowledgeID") Long knowledgeID);

    @PUT("/api/knowledge-bases")
    Call<Void> updateKnowledge(@Body UpdateKnowledgeRequest request);

    @POST("/api/knowledge-bases")
    Call<Void> addKnowledge(@Body AddKnowledgeRequest request);

    @DELETE("/api/knowledge-bases/{knowledgeID}")
    Call<Void> deleteKnowledge(@Path("knowledgeID") Long knowledgeID);


    // PROFILE
    @PUT("/api/profiles")
    Call<Void> updateProfile(@Body UpdateProfileRequest request);


    // TICKET
    @GET("/api/tickets/user")
    Call<List<Ticket>> getUserTickets();

    @GET("/api/tickets")
    Call<List<Ticket>> getAllTickets();

    @GET("/api/tickets/{ticketID}")
    Call<Ticket> getTicketById(@Path("ticketID") Long ticketID);

    @DELETE("/api/tickets/{ticketID}")
    Call<Void> deleteTicket(@Path("ticketID") Long ticketID);

    @POST("/api/tickets")
    Call<Void> createTicket(@Body AddTicketRequest request);

    @PUT("/api/tickets")
    Call<Void> updateTicket(@Body UpdateTicketRequest request);

    @POST("/api/tickets/status")
    Call<Void> changeTicketStatus(@Body UpdateTicketStatusRequest request);


    // TICKET REPLY
    @DELETE("/api/tickets/reply/{replyID}")
    Call<Void> deleteReply(@Path("replyID") Long replyID);

    @POST("/api/tickets/reply")
    Call<Void> addTicketReply(@Body AddTicketReplyRequest request);


    // TICKET IMAGE
    @Multipart
    @POST("/api/tickets/{ticketID}/image")
    Call<Void> uploadTicketImages(
            @Path("ticketID") Long ticketId,
            @Part List<MultipartBody.Part> files
    );

    @DELETE("/api/tickets/image/{imageID}")
    Call<Void> deleteTicketImage(@Path("imageID") Long imageId);


    // TICKET STATUS
    @GET("/api/statuses")
    Call<List<Status>> getAllStatuses();

    @GET("/api/statuses/use")
    Call<List<StatusResponse>> getStatusesWithUseNumber();

    @PUT("/api/statuses")
    Call<Void> updateStatus(@Body UpdateStatusRequest request);

    @POST("/api/statuses")
    Call<Void> addStatus(@Body AddStatusRequest request);

    @DELETE("/api/statuses/{statusID}")
    Call<Void> deleteStatus(@Path("statusID") Long statusID);


    // CATEGORY
    @GET("/api/categories")
    Call<List<Category>> getAllCategories();

    @GET("/api/categories/use")
    Call<List<CategoryResponse>> getAllCategoriesWithUseNumber();

    @PUT("/api/categories")
    Call<Void> updateCategory(@Body UpdateCategoryRequest request);

    @POST("/api/categories")
    Call<Void> addCategory(@Body AddCategoryRequest request);

    @DELETE("/api/categories/{categoryID}")
    Call<Void> deleteCategory(@Path("categoryID") Long categoryID);


    // PRIORITY
    @GET("/api/priorities")
    Call<List<Priority>> getAllPriorities();

    @GET("/api/priorities/use")
    Call<List<PriorityResponse>> getAllPrioritiesWithUseNumber();

    @PUT("/api/priorities")
    Call<Void> updatePriority(@Body UpdatePriorityRequest request);

    @POST("/api/priorities")
    Call<Void> addPriority(@Body AddPriorityRequest request);

    @DELETE("/api/priorities/{priorityID}")
    Call<Void> deletePriority(@Path("priorityID") Long priorityID);

}
