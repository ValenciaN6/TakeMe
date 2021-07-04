package com.example.TakeMe;

public class Request {
    public String requester;
    public String type;
    public String response;
    public String date;
    public String action;
    private String ID;

    public Request(String requester, String type, String response, String date, String action, String ID) {
        this.requester = requester;
        this.type = type;
        this.response = response;
        this.date = date;
        this.action = action;
        this.ID = ID;
    }


}
