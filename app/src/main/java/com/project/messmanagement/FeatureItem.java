package com.project.messmanagement;

public class FeatureItem {
    private String label;
    private int iconRes;
    private int bgRes;

    public FeatureItem(String label, int iconRes, int bgRes) {
        this.label = label;
        this.iconRes = iconRes;
        this.bgRes = bgRes;
    }

    public String getLabel() { return label; }
    public int getIconRes() { return iconRes; }
    public int getBgRes() { return bgRes; }
}