package com.happyhappyyay.landscaperecord.user

import androidx.lifecycle.ViewModel
import com.happyhappyyay.landscaperecord.database.user.User

class UserViewModel: ViewModel() {
    val users = ArrayList<User>()
    init {
        var user = User(first = "dpig",last = "eric", password = "12345")
        users.add(user)
        var user1 = User(first = "joor", last = "gah", password = "00000")
        users.add(user1)
        var user2 = User(first = "wooo", last = "awrrr", password = "password")
        users.add(user2)
    }
//    present bottom navigation for single logged-in user or multiple users
}