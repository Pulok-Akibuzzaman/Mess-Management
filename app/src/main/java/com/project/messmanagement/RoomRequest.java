package com.project.messmanagement;

public class RoomRequest {
    int id;
    String memberName, roomNo, issue, priority, status, date;

    public RoomRequest(int id, String memberName, String roomNo, String issue, String priority, String status, String date) {
        this.id = id;
        this.memberName = memberName;
        this.roomNo = roomNo;
        this.issue = issue;
        this.priority = priority;
        this.status = status;
        this.date = date;
    }
}
