package com.happyhappyyay.landscaperecord.database.customer.service

import com.happyhappyyay.landscaperecord.pojo.Material

data class Service (
    val id: Int,
    var services: String,
    val username:String,
    val customerName:String ,
    val materialCost: Double = 0.0,
     val manHours: Double = 0.0,
     val mi: Double = 0.0,
     val amountPaid: Double = 0.0,
     val price: Double = 0.0,
     val materials: List<Material> = ArrayList(),
     val startTime: Long = System.currentTimeMillis(),
     val endTime: Long = 0
)