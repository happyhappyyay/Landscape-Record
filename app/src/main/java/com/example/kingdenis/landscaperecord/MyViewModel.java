package com.example.kingdenis.landscaperecord;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {
    private List<User> users;

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        users.add(new User());
        users.add(new User());
        users.add(new User());
        users.get(0).setName("bob");
        users.get(1).setName("Harry");
        users.get(2).setName("george");
    }
}
