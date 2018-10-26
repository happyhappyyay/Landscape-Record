package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class User implements DatabaseObjects<User> {
    @PrimaryKey(autoGenerate = true)
    private int userId;
    private double hours;
    private String firstName;
    private String lastName;
    private String name;
    private String password;
    private boolean admin;
    private long startTime;
    private String nickname;

    public User() {
        nickname = "";
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return !getNickname().isEmpty() ? nickname : name;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<User> retrieveAllClassInstancesFromDatabase(AppDatabase db) {
        return db.userDao().getAllUsers();
    }

    @Override
    public User retrieveClassInstanceFromDatabaseID(AppDatabase db, int id) {
        return db.userDao().findUserByID(id);
    }

    @Override
    public User retrieveClassInstanceFromDatabaseString(AppDatabase db, String string) {
        return db.userDao().findUserByName(string);
    }

    @Override
    public void deleteClassInstanceFromDatabase(User objectToDelete, AppDatabase db) {
        db.userDao().deleteUser(objectToDelete);
    }

    @Override
    public void updateClassInstanceFromDatabase(User objectToUpdate, AppDatabase db) {
        db.userDao().updateUser(objectToUpdate);
    }

    @Override
    public void insertClassInstanceFromDatabase(User objectToInsert, AppDatabase db) {
        db.userDao().insert(objectToInsert);
    }
}
