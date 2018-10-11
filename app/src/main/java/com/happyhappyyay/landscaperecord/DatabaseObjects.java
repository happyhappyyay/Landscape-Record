package com.happyhappyyay.landscaperecord;

import android.content.Context;

import java.util.List;

public interface DatabaseObjects <T extends DatabaseObjects<T>> {
    String getName();
    List<T> retrieveAllClassInstancesFromDatabase(AppDatabase db);
    T retrieveClassInstanceFromDatabase(AppDatabase db, int id);
    void deleteClassInstanceFromDatabase(T objectToDelete, AppDatabase db);
    void updateClassInstanceFromDatabase(T objectToUpdate, AppDatabase db);
    void insertClassInstanceFromDatabase(T objectToInsert, AppDatabase db);
}
