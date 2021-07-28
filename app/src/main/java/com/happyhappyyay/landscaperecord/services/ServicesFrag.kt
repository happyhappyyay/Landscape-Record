package com.happyhappyyay.landscaperecord.services

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.happyhappyyay.landscaperecord.MainActivity
import com.happyhappyyay.landscaperecord.R
import com.happyhappyyay.landscaperecord.databinding.FragmentServicesBinding
import com.happyhappyyay.landscaperecord.pojo.Service


class ServicesFrag : Fragment() {
    private lateinit var viewModel: ServicesViewModel
    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ServicesViewModel::class.java)
        binding.floatingActionButton4.setOnClickListener{
            findNavController().navigate(ServicesFragDirections.actionServicesFragToAddService())
        }
        val adapter = ServicesAdapter(viewModel.services)
        binding.recyclerViewServices.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search, menu)
        setupSearchBar(menu)
    }

    private fun setupSearchBar(menu: Menu){
        val item = menu.findItem(R.id.app_bar_search)
        val searchView = SearchView((context as MainActivity).supportActionBar!!.themedContext)
        searchView.setBackgroundColor(0xFFFFFFF)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        searchView.isIconifiedByDefault = false
        searchView.queryHint = resources.getString(R.string.search_hint)
        item.actionView = searchView
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                searchView.requestFocus()
                (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.
                toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                (activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.
                toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(activity,query,Toast.LENGTH_LONG).show()
                viewModel.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }
}