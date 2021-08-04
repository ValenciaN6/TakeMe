package com.example.TakeMe;

public class Request {
    public String requester;
    public String type;
    public int status;
    public String date;
    public String ID;
    public Double latitude;
    public Double longitude;
    public String NextOfKinName;
    public String  NextOfKinCell;
    public String toID;


    public Request(String requester, String type, int status, String date, String ID, Double latitude, Double longitude, String nextOfKinName, String nextOfKinCell, String toID) {
        this.requester = requester;
        this.type = type;
        this.status = status;
        this.date = date;
        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        NextOfKinName = nextOfKinName;
        NextOfKinCell = nextOfKinCell;
        this.toID = toID;

    }

    public Request(String requester, String type, int status, String date, String ID, Double latitude, Double longitude, String nextOfKinName, String nextOfKinCell) {
        this.requester = requester;
        this.type = type;
        this.status = status;
        this.date = date;
        this.ID = ID;
        this.latitude = latitude;
        this.longitude = longitude;
        NextOfKinName = nextOfKinName;
        NextOfKinCell = nextOfKinCell;
    }


}
