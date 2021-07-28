package com.happyhappyyay.landscaperecord.services

import androidx.lifecycle.ViewModel
import com.happyhappyyay.landscaperecord.pojo.Service

class ServicesViewModel: ViewModel() {
    val services = ArrayList<Service>()

    init {
        var service = Service()
        service.customerName = "george"
        service.id = 1
        services.add(service)
    }
    fun search(query: String){

    }
}