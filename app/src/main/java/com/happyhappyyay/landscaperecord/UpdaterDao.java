package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

@Dao
public interface UpdaterDao {
    @Insert
    void insert(DatabaseUpdaterObject... objects);


    @Update
    void updateDatabaseUpdaterObject(DatabaseUpdaterObject databaseUpdaterObject);

    @Delete
    void deleteDatabaseUpdaterObject(DatabaseUpdaterObject databaseUpdaterObject);

}
