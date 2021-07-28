package com.happyhappyyay.landscaperecord.addservice

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.landscaperecord.MainActivity
import com.happyhappyyay.landscaperecord.accounts.AccountsFragDirections
import com.happyhappyyay.landscaperecord.databinding.FragmentAddServiceBinding

class AddService : Fragment() {
    private lateinit var viewModel: AddServiceViewModel
    private var _binding: FragmentAddServiceBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNav()
    }

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showBottomNav()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddServiceBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AddServiceViewModel::class.java)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}