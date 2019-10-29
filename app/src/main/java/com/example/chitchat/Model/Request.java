package com.example.chitchat.Model;

public class Request {
    String request_type;

    public Request(){

    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public Request(String request_type) {
        this.request_type = request_type;
    }
}
