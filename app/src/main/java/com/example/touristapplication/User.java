package com.example.touristapplication;

public class User {
    public String userName, email, phone, password, imageUrl ,category;
    public String accounttype;
    public String status;


    public User() {

    }
   public User(String username, String email, String phone, String password, String accounttype , String category , String status) {

        this.userName = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.accounttype = accounttype;
        this.category = category;
        this.status = status;


    }

    public User(String username, String email, String phone, String password, String accounttype , String status ) {

        this.userName = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.accounttype = accounttype;
        this.status = status;


    }


    public String getPhone() {
        return phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userId) {
        this.userName = userId;
    }


}
