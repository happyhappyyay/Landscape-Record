package com.happyhappyyay.landscaperecord.database.user

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*
import com.happyhappyyay.landscaperecord.utility.MILLISECONDSTOHOURS

@Entity(tableName = "user_table")
data class User(
        @PrimaryKey(autoGenerate = true)
        val id: String = UUID.randomUUID().toString(),
        var hours: Double = 0.0,
        var first: String,
        var last: String,
        var password: String,
        var admin: Boolean = false,
        var startTime: Long = 0,
        var nickname: String = "",
        var modifiedTime: Long = System.currentTimeMillis()
)