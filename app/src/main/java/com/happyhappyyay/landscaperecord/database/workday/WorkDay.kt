package com.happyhappyyay.landscaperecord.database.workday

import androidx.room.PrimaryKey
import java.util.*

class WorkDay(
        @PrimaryKey
        var id: String = UUID.randomUUID().toString(),
        val dateTime: Long,
        val userHours: Map<String, Int> = HashMap(),
        val payments: Map<String, Double> = HashMap(),
        val expenses: Map<String, Double> = HashMap(),
        val services: Map<String, String> = HashMap(),
        var modifiedTime: Long = System.currentTimeMillis()
)