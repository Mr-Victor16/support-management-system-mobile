package com.example.support_management_system_mobile.ui.management.user;

import com.example.support_management_system_mobile.data.payload.response.UserDetailsResponse;

import java.util.List;

public class UserListUIState {
    private UserListUIState() { }

    public static class Loading extends UserListUIState {   }

    public static class Success extends UserListUIState {
        public final List<UserDetailsResponse> userList;
        public final boolean canManage;

        public Success(List<UserDetailsResponse> items, boolean canManage) {
            this.userList = items;
            this.canManage = canManage;
        }
    }

    public static class Error extends UserListUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
