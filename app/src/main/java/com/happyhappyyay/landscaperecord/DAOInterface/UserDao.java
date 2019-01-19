package com.happyhappyyay.landscaperecord.DAOInterface;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.happyhappyyay.landscaperecord.POJO.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User... users);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE name = :username")
    User findUserByName(String username);

    @Query("SELECT * FROM User WHERE userId = :userId")
    User findUserByID(String userId);

    @Query("SELECT * FROM User WHERE modifiedTime > :modifiedTime")
    List<User> getNewlyModifiedUsers(long modifiedTime);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM User")
    void deleteAllUsers();

}
