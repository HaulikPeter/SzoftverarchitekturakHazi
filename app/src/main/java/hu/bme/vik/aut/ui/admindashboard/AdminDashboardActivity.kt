package hu.bme.vik.aut.ui.admindashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.ActivityAdminDashboardBinding
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminOverviewFragment
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminResidentsFragment
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminSupplyFragment
import kotlin.math.log

class AdminDashboardActivity : AppCompatActivity() {
    // code created with help from: https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
    lateinit var binding:  ActivityAdminDashboardBinding
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        val bottomNavigationBar = binding.bottomNavBar

        val overviewFragment = AdminOverviewFragment()
        val residentsFragment = AdminResidentsFragment()
        val supplyFragment = AdminSupplyFragment()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationBar.setOnItemSelectedListener {
            if (bottomNavigationBar.selectedItemId == it.itemId) {
                false
            } else {
                navigateToFragment(getFragmentNameForMenuItemId(bottomNavigationBar.selectedItemId), getFragmentNameForMenuItemId(it.itemId))
                true
            }
        }

        setContentView(binding.root)
    }

    private fun navigateToFragment(currentFragmentName: String, nextFragmentName: String) {
        val navigationName = "action_${currentFragmentName}_to_${nextFragmentName}"
        Log.d("ANYAD", navigationName)
        val navigationId = resources.getIdentifier(navigationName,"id",packageName)
        Log.d("ANYAD", "$navigationId")
        navController.navigate(navigationId)
    }

    private fun getFragmentNameForMenuItemId(menuItemId: Int): String {
        return when (menuItemId) {
            R.id.admin_overview_menu_item -> resources.getResourceEntryName(R.id.adminOverviewFragment)
            R.id.admin_residents_menu_item -> resources.getResourceEntryName(R.id.adminResidentsFragment)
            R.id.admin_supply_menu_item -> resources.getResourceEntryName(R.id.adminSupplyFragment)
            else -> resources.getResourceEntryName(R.id.adminOverviewFragment)
        }
    }

}