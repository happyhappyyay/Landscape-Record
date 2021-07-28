package com.happyhappyyay.landscaperecord.customer


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyhappyyay.landscaperecord.database.customer.Customer
import com.happyhappyyay.landscaperecord.databinding.CustomerItemBinding

class CustomersAdapter(private val customers : List<Customer>): RecyclerView.Adapter<CustomersAdapter.ViewHolder>(){
    class ViewHolder private constructor(val binding: CustomerItemBinding):RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from(parent: ViewGroup):ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CustomerItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
        fun bind(customer: Customer){
            binding.contactItemName.text = customer.first
            binding.contactItemAddress.text = customer.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(customers[position])
    }

    override fun getItemCount(): Int {
        return customers.size
    }
}