package com.project.messmanagement;

public class Member {
    int id;
    String name, initials, room, phone, due, status, email;
    int meals;

    public Member(int id, String name, String initials, String room, String phone, int meals, String due, String status, String email) {
        this.id       = id;
        this.name     = name;
        this.initials = initials;
        this.room     = room;
        this.phone    = phone;
        this.meals    = meals;
        this.due      = due;
        this.status   = status;
        this.email    = email;
    }
}
