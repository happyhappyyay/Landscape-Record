package com.example.kingdenis.landscaperecord;

import android.content.Context;

public class Authentication {
    private User user;
    private static Authentication instance;
    public static Authentication getAuthentication(Context context) {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

