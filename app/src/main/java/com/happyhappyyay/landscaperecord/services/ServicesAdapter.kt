package com.happyhappyyay.landscaperecord.services

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyhappyyay.landscaperecord.databinding.ServicesRecyclerItemBinding
import com.happyhappyyay.landscaperecord.pojo.Service

class ServicesAdapter(private val services : List<Service>): RecyclerView.Adapter<ServicesAdapter.ViewHolder>(){
        class ViewHolder private constructor(val binding: ServicesRecyclerItemBinding): RecyclerView.ViewHolder(binding.root){
            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ServicesRecyclerItemBinding.inflate(layoutInflater, parent, false)
                    return ViewHolder(binding)
                }
            }
            fun bind (service: Service){
                binding.textView53.text = service.customerName
                binding.textView54.text = service.id.toString()
                binding.textView56.text = "Ate some donuts... jk. Steak."
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(services[position])
    }

    override fun getItemCount(): Int {
        return services.size
    }
}