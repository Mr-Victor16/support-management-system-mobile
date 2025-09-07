package com.example.support_management_system_mobile.ui.management.software;

import com.example.support_management_system_mobile.payload.response.SoftwareResponse;

import java.util.List;

public abstract class SoftwareListUIState {
    private SoftwareListUIState() { }

    public static class Loading extends SoftwareListUIState {   }

    public static class Success extends SoftwareListUIState {
        public final List<SoftwareResponse> softwareList;
        public final boolean canManage;

        public Success(List<SoftwareResponse> items, boolean canManage) {
            this.softwareList = items;
            this.canManage = canManage;
        }
    }

    public static class Error extends SoftwareListUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
