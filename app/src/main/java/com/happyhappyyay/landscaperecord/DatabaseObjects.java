package com.happyhappyyay.landscaperecord;

import java.util.List;

public interface DatabaseObjects <T extends DatabaseObjects<T>> {
    String getName();
    List<T> retrieveAllClassInstancesFromDatabase(DatabaseOperator db);
    T retrieveClassInstanceFromDatabaseID(DatabaseOperator db, int id);
    T retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string);
    void deleteClassInstanceFromDatabase(DatabaseOperator db, T objectToDelete);
    void updateClassInstanceFromDatabase(DatabaseOperator db, T objectToUpdate);
    void insertClassInstanceFromDatabase(DatabaseOperator db, T objectToInsert);
}
