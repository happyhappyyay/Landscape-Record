package com.happyhappyyay.landscaperecord.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.landscaperecord.databinding.FragmentDashboardBinding

class DashboardFrag : Fragment() {
    private lateinit var viewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        binding.button6.setOnClickListener {
            this.findNavController().navigate(DashboardFragDirections.actionDashboardFragToCustomerFrag())
        }
        binding.button9.setOnClickListener {
            this.findNavController().navigate(DashboardFragDirections.actionDashboardFragToUserFrag())
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}