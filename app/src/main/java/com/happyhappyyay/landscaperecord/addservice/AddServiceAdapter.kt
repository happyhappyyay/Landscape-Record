package com.happyhappyyay.landscaperecord.addservice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyhappyyay.landscaperecord.databinding.SeasonSelectionTemplateBinding
val SEASONS = arrayOf("Property Maintenance", "Landscaping", "Snow Removal")
class AddServiceAdapter: RecyclerView.Adapter<AddServiceAdapter.ViewHolder>(){
        class ViewHolder private constructor(val binding: SeasonSelectionTemplateBinding): RecyclerView.ViewHolder(binding.root){
            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = SeasonSelectionTemplateBinding.inflate(layoutInflater, parent, false)
                    return ViewHolder(binding)
                }
            }

            fun bind(position: Int){
                binding.seasonTitle.text = SEASONS[position]
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            return holder.bind(position)
        }

        override fun getItemCount(): Int {
            return SEASONS.size
        }
}