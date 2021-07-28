package com.happyhappyyay.landscaperecord.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.landscaperecord.databinding.FragmentAccountsBinding

class AccountsFrag : Fragment() {
    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        binding.accountsCustomerImage.setOnClickListener {
            findNavController().navigate(AccountsFragDirections.actionAccountsFragToCustomerFrag())
        }
        binding.accountsUserImage.setOnClickListener {
            findNavController().navigate((AccountsFragDirections.actionAccountsFragToUserFrag()))
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}