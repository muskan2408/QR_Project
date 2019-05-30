package com.qrtool.qrproject.util;

public class History {

    private String date,time,rname,raddress;
    private double latitude ,longitude;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public History(String date, String time, String rname, double latitude, double longitude, String raddress) {
        this.date = date;
        this.time = time;
        this.rname = rname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.raddress=raddress;
    }

    public History(String date, String time, String rname) {
        this.date = date;
        this.time = time;
        this.rname = rname;
    }

    public String getRaddress() {
        return raddress;
    }

    public void setRaddress(String raddress) {
        this.raddress = raddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
