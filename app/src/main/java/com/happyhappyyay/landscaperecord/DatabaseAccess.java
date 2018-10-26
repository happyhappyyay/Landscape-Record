package com.happyhappyyay.landscaperecord;

import android.content.Context;

import java.util.List;

public interface DatabaseAccess <T extends DatabaseObjects<T>> {
    Context getContext();
    String createLogInfo();
    void onPostExecute(List<T> databaseObjects);
}
