package com.happyhappyyay.landscaperecord.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyhappyyay.landscaperecord.database.user.User
import com.happyhappyyay.landscaperecord.databinding.UserItemBinding

class UsersAdapter(val users : List<User>): RecyclerView.Adapter<UsersAdapter.ViewHolder>(){
    class ViewHolder private constructor(val binding: UserItemBinding):RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from (parent: ViewGroup):ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserItemBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }

        fun bind(user: User) {
            binding.userItemName.text = user.first
            binding.userItemHours.text = user.hours.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
}