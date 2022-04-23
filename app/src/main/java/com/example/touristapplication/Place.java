package com.example.touristapplication;

import java.util.ArrayList;
import java.util.List;

public class Place {
    String id ,ownerName , place_name, descName,contact,st_day, end_day,   fromtime, totime ,imageUrl , post_emailId;
    List<String> imgTags;



    public Place() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }





    public Place(String id , String ownerName, String place_name, String descName, String contact, String st_day, String end_day, String fromtime, String totime , String imageUrl , List<String> imgTags , String post_emailId ) {
        this.id = id;
        this.ownerName = ownerName;

        this.place_name = place_name;
        this.descName = descName;
        this.contact = contact;
        this.st_day = st_day;
        this.end_day = end_day;
        this.fromtime = fromtime;
        this.totime = totime;
        this.imageUrl = imageUrl;
        this.imgTags = imgTags;
        this.post_emailId = post_emailId;

    }

    public String getPost_emailId() {
        return post_emailId;
    }

    public List<String> getImgTags() {
        return imgTags;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwnerId() {
        return id;
    }

    public void setOwnerId(String id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getSt_day() {
        return st_day;
    }

    public void setSt_day(String st_day) {
        this.st_day = st_day;
    }

    public String getEnd_day() {
        return end_day;
    }

    public void setEnd_day(String end_day) {
        this.end_day = end_day;
    }

    public String getDescName() {
        return descName;
    }

    public void setDescName(String descName) {
        this.descName = descName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFromtime() {
        return fromtime;
    }

    public void setFromtime(String fromtime) {
        this.fromtime = fromtime;
    }

    public String getTotime() {
        return totime;
    }

    public void setTotime(String totime) {
        this.totime = totime;
    }
}
