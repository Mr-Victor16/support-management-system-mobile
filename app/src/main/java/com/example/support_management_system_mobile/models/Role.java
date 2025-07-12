package com.example.support_management_system_mobile.models;

public class Role {
    private Long id;
    private Types type;

    public Role(Types type){
        this.type = type;
    }

    public enum Types{
        ROLE_ADMIN,
        ROLE_OPERATOR,
        ROLE_USER
    }

    public Role(Long id, Types type) {
        this.id = id;
        this.type = type;
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

    public void setType(Types type) {
        this.type = type;
    }
}
