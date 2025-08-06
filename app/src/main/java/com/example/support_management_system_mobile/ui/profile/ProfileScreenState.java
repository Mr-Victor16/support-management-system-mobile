package com.example.support_management_system_mobile.ui.profile;

public interface ProfileScreenState {
    class Loading implements ProfileScreenState {}

    class Success implements ProfileScreenState {
        public final ProfileViewModel.ProfileUIState userData;

        public Success(ProfileViewModel.ProfileUIState userData) {
            this.userData = userData;
        }
    }
}
