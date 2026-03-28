package net.shlab.hogefugapiyo.equipmentlending.model.value;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isUser() {
        return this == USER;
    }

    public static UserRole fromCode(String code) {
        for (UserRole userRole : values()) {
            if (userRole.code.equals(code)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + code);
    }
}
