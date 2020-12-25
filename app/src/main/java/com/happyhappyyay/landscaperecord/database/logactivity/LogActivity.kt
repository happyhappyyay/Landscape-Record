package com.happyhappyyay.landscaperecord.database.logactivity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class LogActivity(
        @PrimaryKey
        var id: String = UUID.randomUUID().toString(),
        val info: String,
        val logActivityAction: Int,
        val logActivityType: Int,
        val modifiedTime: Long = System.currentTimeMillis(),
        val username: String,
        val objId: String,
)