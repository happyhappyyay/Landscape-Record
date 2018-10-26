package com.happyhappyyay.landscaperecord;

public interface  MultiDatabaseAccess <T extends DatabaseObjects<T>> extends DatabaseAccess <T> {
    void accessDatabaseMultipleTimes();
    void createCustomLog();
}
