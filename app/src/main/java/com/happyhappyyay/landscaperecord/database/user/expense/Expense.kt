package com.happyhappyyay.landscaperecord.database.user.expense

data class Expense(
        val price: Double = 0.0,
        val name: String,
        val expenseType: String,
        val paymentType: String,
        val date: Long = 0,
        var modifiedTime: Long = 0,
)