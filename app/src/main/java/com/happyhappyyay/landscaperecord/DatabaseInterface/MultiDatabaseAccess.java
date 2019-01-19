package com.happyhappyyay.landscaperecord.DatabaseInterface;

public interface  MultiDatabaseAccess <T extends DatabaseObjects<T>> extends DatabaseAccess<T> {
    void accessDatabaseMultipleTimes();
    void createCustomLog();
}
