package com.example.support_management_system_mobile.ui.profile;

public class Event {
    private boolean hasBeenHandled = false;

    public boolean handle() {
        if (hasBeenHandled) {
            return false;
        } else {
            hasBeenHandled = true;
            return true;
        }
    }
}
