package com.project.messmanagement;

public class ProfileField {
    String label;
    String value;
    int iconRes;
    boolean isEditable;

    public ProfileField(String label, String value, int iconRes, boolean isEditable) {
        this.label = label;
        this.value = value;
        this.iconRes = iconRes;
        this.isEditable = isEditable;
    }
}
