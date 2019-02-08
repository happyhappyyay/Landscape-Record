package com.happyhappyyay.landscaperecord.database_interface;

public interface  MultiDatabaseAccess <T extends DatabaseObjects<T>> extends DatabaseAccess<T> {
    void accessDatabaseMultipleTimes();
    void createCustomLog();
}
