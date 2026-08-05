package com.project.messmanagement;

public class GuestMeal {
    public int id;
    public String memberName;
    public String guestName;
    public int mealCount;
    public String mealType;
    public String date;

    public GuestMeal(int id, String memberName, String guestName, int mealCount, String mealType, String date) {
        this.id = id;
        this.memberName = memberName;
        this.guestName = guestName;
        this.mealCount = mealCount;
        this.mealType = mealType;
        this.date = date;
    }
}
