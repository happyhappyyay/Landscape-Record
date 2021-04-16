package com.happyhappyyay.landscaperecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.happyhappyyay.landscaperecord.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = this.findNavController(R.id.myNavHost)
        val bottomNav = binding.bottomNavigation
        val appBarConfiguration = AppBarConfiguration(
                topLevelDestinationIds = setOf (
                        R.id.dashboardFrag,
                        R.id.servicesFrag,
                        R.id.checkInFrag,
                        R.id.accountsFrag,
                        R.id.calendarFrag
                )
        )
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration)
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.dashboardFrag) {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
            else if(destination.id == R.id.loginFrag) {
                binding.bottomNavigation.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHost)
        return navController.navigateUp()
    }

    fun showBottomNav(){
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideBottomNav(){
        binding.bottomNavigation.visibility = View.GONE
    }
}