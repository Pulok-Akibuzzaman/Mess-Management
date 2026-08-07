package com.project.messmanagement;

public class Notice {
    int id;
    String title, content, priority, audience, date;

    public Notice(int id, String title, String content, String priority, String audience, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.audience = audience;
        this.date = date;
    }
}
