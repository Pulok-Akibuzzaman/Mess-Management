package com.project.messmanagement;

public class StatItem {
    private String label;
    private String value;
    private boolean isUrgent;

    public StatItem(String label, String value, boolean isUrgent) {
        this.label = label;
        this.value = value;
        this.isUrgent = isUrgent;
    }

    public String getLabel() { return label; }
    public String getValue() { return value; }
    public boolean isUrgent() { return isUrgent; }
}