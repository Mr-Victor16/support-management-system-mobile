package com.example.support_management_system_mobile.ui.management.status;

import com.example.support_management_system_mobile.data.payload.response.StatusResponse;

import java.util.List;

public abstract class StatusListUIState {
    private StatusListUIState() { }

    public static class Loading extends StatusListUIState {   }

    public static class Success extends StatusListUIState {
        public final List<StatusResponse> statusList;
        public final boolean canManage;

        public Success(List<StatusResponse> items, boolean canManage) {
            this.statusList = items;
            this.canManage = canManage;
        }
    }

    public static class Error extends StatusListUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
