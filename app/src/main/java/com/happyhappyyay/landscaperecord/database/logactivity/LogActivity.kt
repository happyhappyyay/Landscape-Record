package com.happyhappyyay.landscaperecord.database.logactivity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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