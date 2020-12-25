package com.happyhappyyay.landscaperecord.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.happyhappyyay.landscaperecord.pojo.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User... users);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE name = :username")
    User findUserByName(String username);

    @Query("SELECT * FROM User WHERE id = :userId")
    User findUserByID(String userId);

    @Query("SELECT * FROM User WHERE modifiedTime > :modifiedTime")
    List<User> getNewlyModifiedUsers(long modifiedTime);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}
