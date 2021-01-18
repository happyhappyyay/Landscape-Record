package com.happyhappyyay.landscaperecord.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.happyhappyyay.landscaperecord.databinding.FragmentCustomerBinding

class CustomerFrag : Fragment() {
    private lateinit var viewModel: CustomerViewModel
    private var _binding: FragmentCustomerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCustomerBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}