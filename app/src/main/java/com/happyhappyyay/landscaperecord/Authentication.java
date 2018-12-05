package com.happyhappyyay.landscaperecord;

public class Authentication {
    private static Authentication instance;
    private User user;

    public static Authentication getAuthentication() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

