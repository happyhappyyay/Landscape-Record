package com.happyhappyyay.landscaperecord.database.customer.service.material

data class Material(
         var name: String,
         val price: Double = 0.0,
         val type: String,
         val add: Boolean = false,
         val quantity: Double = 0.0,
         val measurement: String,
)