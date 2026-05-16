package com.relic.enumeration;

public enum UserTypeEnum {
    ADMIN("admin", "后台管理员"),
    KNOWLEDGE("knowledge", "知识服务子系统用户"),
    MUSEUM("museum", "掌上博物馆用户");

    private final String code;
    private final String displayName;

    UserTypeEnum(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserTypeEnum fromCode(String code) {
        for (UserTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的用户类型: " + code);
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isFrontUser() {
        return this == KNOWLEDGE || this == MUSEUM;
    }
}