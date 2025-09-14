package com.example.support_management_system_mobile.ui.management.priority;

import com.example.support_management_system_mobile.data.payload.response.PriorityResponse;

import java.util.List;

public abstract class PriorityListUIState {
    private PriorityListUIState() {}

    public static class Loading extends PriorityListUIState {}

    public static class Success extends PriorityListUIState {
        public final List<PriorityResponse> priorities;

        public Success(List<PriorityResponse> priorities) {
            this.priorities = priorities;
        }
    }

    public static class Error extends PriorityListUIState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
