package com.happyhappyyay.landscaperecord.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.happyhappyyay.landscaperecord.databinding.FragmentUserBinding

class UserFrag : Fragment() {
    private lateinit var viewModel: UserViewModel
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        return binding.root
    }
}