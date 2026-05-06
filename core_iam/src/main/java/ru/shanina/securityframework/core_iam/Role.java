package ru.shanina.securityframework.core_iam;

public enum Role {
    ADMIN("ADMIN", 100),
    USER("USER", 10),
    GUEST("GUEST", 1);

    private final String name;
    private final int level;

    Role(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean hasPermission(Role required) {
        return this.level >= required.level;
    }
}
