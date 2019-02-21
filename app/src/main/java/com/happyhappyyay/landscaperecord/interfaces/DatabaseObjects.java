package com.happyhappyyay.landscaperecord.interfaces;

import java.util.List;

public interface DatabaseObjects <T extends DatabaseObjects<T>> {
    String getName();
    List<T> retrieveAllClassInstancesFromDatabase(DatabaseOperator db);
    List<T> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime);
    T retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id);
    T retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string);
    void deleteClassInstanceFromDatabase(DatabaseOperator db, T objectToDelete);
    void updateClassInstanceFromDatabase(DatabaseOperator db, T objectToUpdate);
    void insertClassInstanceFromDatabase(DatabaseOperator db, T objectToInsert);
    String getId();
    long getModifiedTime();
    void setModifiedTime(long modifiedTime);
}
