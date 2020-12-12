package com.happyhappyyay.landscaperecord.database.customer

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.happyhappyyay.landscaperecord.pojo.Payment
import java.util.*

@Entity(tableName = "customer_table")
data class Customer(
        @PrimaryKey(autoGenerate = true)
        val id: String = UUID.randomUUID().toString(),
        var first: String,
        var last: String,
        var address: String,
        var city: String = "",
        var phone: String = "",
        var email: String = "",
        var business: String = "",
        var day: String = "",
        var state: String = "",
        var mi: Double = 0.0,
        var modifiedTime: Long = System.currentTimeMillis(),
        val services: List<String> = ArrayList(),
        val payment: Payment = Payment()

)