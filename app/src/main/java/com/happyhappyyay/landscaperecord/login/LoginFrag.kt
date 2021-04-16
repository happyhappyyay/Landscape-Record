package com.happyhappyyay.landscaperecord.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.landscaperecord.R
import com.happyhappyyay.landscaperecord.databinding.FragmentLoginBinding


class LoginFrag : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        requireActivity().window.setBackgroundDrawableResource(R.drawable.landscape_record)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.button4.setOnClickListener {
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.colorPrimary)
            (requireActivity() as AppCompatActivity).supportActionBar?.show()
            requireActivity().window.setBackgroundDrawable(ColorDrawable(Color.DKGRAY))
            this.findNavController().navigate(LoginFragDirections.actionLoginFragToDashboardFrag())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}