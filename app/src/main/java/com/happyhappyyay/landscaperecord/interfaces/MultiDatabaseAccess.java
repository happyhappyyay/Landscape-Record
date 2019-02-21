package com.happyhappyyay.landscaperecord.interfaces;

public interface  MultiDatabaseAccess <T extends DatabaseObjects<T>> extends DatabaseAccess<T> {
    void accessDatabaseMultipleTimes();
    void createCustomLog();
}
