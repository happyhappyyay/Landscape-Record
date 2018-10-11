package com.happyhappyyay.landscaperecord;

import android.content.Context;

import java.util.List;

public interface DatabaseAccess <T extends DatabaseObjects<T>> {
    Context getContext();
    void setObjectsToAccessor(List<T> databaseObjects);
}
