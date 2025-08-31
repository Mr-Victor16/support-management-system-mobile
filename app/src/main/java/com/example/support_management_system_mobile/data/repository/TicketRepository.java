package com.example.support_management_system_mobile.data.repository;

import android.net.Uri;

import com.example.support_management_system_mobile.R;
import com.example.support_management_system_mobile.models.Ticket;
import com.example.support_management_system_mobile.network.APIService;
import com.example.support_management_system_mobile.payload.request.add.AddTicketReplyRequest;
import com.example.support_management_system_mobile.payload.request.add.AddTicketRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateTicketRequest;
import com.example.support_management_system_mobile.payload.request.update.UpdateTicketStatusRequest;
import com.example.support_management_system_mobile.utils.FilePreparer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.Callback;

public class TicketRepository {
    private final APIService apiService;
    private final FilePreparer filePreparer;

    @Inject
    public TicketRepository(APIService apiService, FilePreparer filePreparer) {
        this.apiService = apiService;
        this.filePreparer = filePreparer;
    }

    public void getUserTickets(Callback<List<Ticket>> callback) {
        apiService.getUserTickets().enqueue(callback);
    }

    public void getAllTickets(Callback<List<Ticket>> callback) {
        apiService.getAllTickets().enqueue(callback);
    }

    public void getTicketById(Long ticketId, Callback<Ticket> callback) {
        apiService.getTicketById(ticketId).enqueue(callback);
    }

    public void addReply(Long ticketId, String content, Callback<Void> callback) {
        apiService.addTicketReply(new AddTicketReplyRequest(ticketId, content)).enqueue(callback);
    }

    public void changeStatus(Long ticketId, Long statusId, Callback<Void> callback) {
        apiService.changeTicketStatus(new UpdateTicketStatusRequest(ticketId, statusId)).enqueue(callback);
    }

    public void deleteTicket(Long ticketId, Callback<Void> callback) {
        apiService.deleteTicket(ticketId).enqueue(callback);
    }

    public void deleteReply(Long replyId, Callback<Void> callback) {
        apiService.deleteReply(replyId).enqueue(callback);
    }

    public void deleteImage(Long imageId, Callback<Void> callback) {
        apiService.deleteTicketImage(imageId).enqueue(callback);
    }

    public void uploadImage(Long ticketId, Uri imageUri, Callback<Void> callback) {
        MultipartBody.Part body = filePreparer.prepareImagePart("files", imageUri);

        if (body == null) {
            callback.onFailure(null, new IOException(String.valueOf(R.string.image_prepare_failed)));
            return;
        }

        apiService.uploadTicketImages(ticketId, Collections.singletonList(body)).enqueue(callback);
    }

    public void createTicket(AddTicketRequest request, Callback<Void> callback) {
        apiService.createTicket(request).enqueue(callback);
    }

    public void updateTicket(UpdateTicketRequest request, Callback<Void> callback) {
        apiService.updateTicket(request).enqueue(callback);
    }
}
