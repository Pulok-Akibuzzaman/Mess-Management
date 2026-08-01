package com.project.messmanagement;

public class RoomRequest {
    String memberName, room, date, issue, status;

    public RoomRequest(String memberName, String room, String date, String issue, String status) {
        this.memberName = memberName;
        this.room = room;
        this.date = date;
        this.issue = issue;
        this.status = status;
    }
}