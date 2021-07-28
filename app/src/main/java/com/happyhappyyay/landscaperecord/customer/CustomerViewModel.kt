package com.happyhappyyay.landscaperecord.customer

import androidx.lifecycle.ViewModel
import com.happyhappyyay.landscaperecord.database.customer.Customer

class CustomerViewModel: ViewModel() {
    val customers = ArrayList<Customer>()
    init {
        val customer = Customer(first = "ba", last = "doo doo", address = "222312 a")
        customers.add(customer)
        val customer1 = Customer(first = "asddd", last = "push", address = "strong lane")
        customers.add(customer1)
        val customer2 = Customer(first = "weekend", last = "boww", address = "lasd")
        customers.add(customer2)
    }
}