package com.exampl.wechat.Models;

public class Users {

    String profilepicture, username, mail, password, userId, lastMessage,Contact="",token;
    String Status = "Hey,there I'm on weChat";



    public Users(String profilepicture, String username, String mail, String password, String userId, String lastMessage, String status,String Contact) {
        this.profilepicture = profilepicture;
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        Status = status;
        this.Contact=Contact;
    }


    //    we need to pass an empty constructor also for firebase

    public Users() {
    }



    //    SignUp Constructor
    public Users(String username, String mail, String password) {
        this.username = username;
        this.password = password;
        this.mail = mail;
    }

    public Users(String username, String mail, String password, String contact) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        Contact = contact;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getUserId(String key) {
//        return userId;
//    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
