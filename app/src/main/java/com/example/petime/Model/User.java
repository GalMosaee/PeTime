package com.example.petime.Model;

public class User {


    private  String id;
    private  String username;
    private  String fullname;
    private  String imageurl;


    public User(String id, String username, String fullname, String imageurl){
        this.id= id;
        this.username= username;
        this.fullname= fullname;
        this.imageurl= imageurl;
    }

    public User(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getImageurl() {
        return imageurl;
    }
}