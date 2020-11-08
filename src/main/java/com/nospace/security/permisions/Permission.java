package com.nospace.security.permisions;

public enum Permission {
    FILE_UPLOAD("file_upload"),
    FILE_REPORT("file_report"),
    FILE_DELETE("file_delete");

    private final String permission;

    private Permission(String permission){
        this.permission = permission;
    }
}