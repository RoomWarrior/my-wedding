package dev.roomwarrior.wedding.enums;

public enum RelationType {
    RELATIVE("Родственник"),
    COLLEAGUE("Коллега"),
    FRIEND("Друг");

    private final String displayName;

    RelationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 