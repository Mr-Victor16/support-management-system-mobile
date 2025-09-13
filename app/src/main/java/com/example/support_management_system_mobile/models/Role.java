package com.example.support_management_system_mobile.models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Role {
    private Long id;
    private final Types type;

    public Role(Types type){
        this.type = type;
    }

    public enum Types{
        ROLE_ADMIN("Administrator"),
        ROLE_OPERATOR("Operator"),
        ROLE_USER("User");

        private final String displayName;

        Types(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Types getType() {
        return type;
    }

    @Override
    @NonNull
    public String toString() {
        return type.getDisplayName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;
        return Objects.equals(id, role.id) && type == role.type;
    }
}
