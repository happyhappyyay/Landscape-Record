package com.happyhappyyay.landscaperecord.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.happyhappyyay.landscaperecord.databinding.FragmentSetupBinding

class SetupFrag : Fragment() {
    private lateinit var viewModel: SetupViewModel
    private var _binding: FragmentSetupBinding? = null
    private val binding get() =  _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetupBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(SetupViewModel::class.java)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}