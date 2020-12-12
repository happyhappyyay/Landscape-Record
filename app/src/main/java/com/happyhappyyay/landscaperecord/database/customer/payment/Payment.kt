package com.happyhappyyay.landscaperecord.database.customer.payment

data class Payment(
        var defaultPrices: Map<String, Double> = HashMap(),
        val dates: List<String> = ArrayList(),
        val amounts: List<Double> = ArrayList(),
        val servicePrices: Map<String, Double> = HashMap(),
        val checkNumbers: List<String> = ArrayList(),
        val paid: Double = 0.0,
        val owed: Double = 0.0,
)