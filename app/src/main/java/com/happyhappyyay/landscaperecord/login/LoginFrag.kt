package com.happyhappyyay.landscaperecord.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.landscaperecord.databinding.FragmentLoginBinding

class LoginFrag : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.button4.setOnClickListener {
            this.findNavController().navigate(LoginFragDirections.actionLoginFragToDashboardFrag())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}