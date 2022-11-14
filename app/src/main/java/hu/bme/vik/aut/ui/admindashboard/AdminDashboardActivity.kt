package hu.bme.vik.aut.ui.admindashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.ActivityAdminDashboardBinding
import hu.bme.vik.aut.ui.admindashboard.data.Resident
import hu.bme.vik.aut.ui.admindashboard.fragments.AddResidentDialogFragment
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminOverviewFragment
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminResidentsFragment
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminSupplyFragment

class AdminDashboardActivity : AppCompatActivity() {
    // code created with help from: https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
    lateinit var binding:  ActivityAdminDashboardBinding
    lateinit var navController: NavController
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        val bottomNavigationBar = binding.bottomNavBar


        navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        floatingActionButton = binding.floatingActionButton

        bottomNavigationBar.setOnItemSelectedListener {
            if (bottomNavigationBar.selectedItemId == it.itemId) {
                false
            } else {
                navigateToFragment(getFragmentIdForMenuItemId(bottomNavigationBar.selectedItemId), getFragmentIdForMenuItemId(it.itemId))
                true
            }
        }

        setContentView(binding.root)
    }

    private fun navigateToFragment(currentFragmentId: Int, nextFragmentId: Int) {
        val currentFragmentName = resources.getResourceEntryName(currentFragmentId)
        val nextFragmentName = resources.getResourceEntryName(nextFragmentId)

        val navigationName = "action_${currentFragmentName}_to_${nextFragmentName}"
        val navigationId = resources.getIdentifier(navigationName,"id",packageName)

        navController.navigate(navigationId)


        if (getIsFabVisibileOnFragment(nextFragmentId)) {
            floatingActionButton.setImageResource(getImageIdForFabOnFragment(nextFragmentId))
            floatingActionButton.setOnClickListener(getOnClickListenerForFabOnFragment(nextFragmentId))
            floatingActionButton.show()
        } else {
            floatingActionButton.hide()
        }

    }
    private fun getIsFabVisibileOnFragment(fragmentId: Int) : Boolean {
        return when (fragmentId) {
            R.id.adminOverviewFragment -> false
            R.id.adminResidentsFragment -> true
            R.id.adminSupplyFragment -> true
            else -> false
        }
    }

    private fun getOnClickListenerForFabOnFragment(fragmentId: Int) : (View) -> Unit {
        Log.d("SZAR", navHostFragment.childFragmentManager.fragments.toString())
        return when (fragmentId) {
            R.id.adminResidentsFragment -> { _: View -> AddResidentDialogFragment(navHostFragment.childFragmentManager.fragments.first() as AdminResidentsFragment).show(this.supportFragmentManager, AddResidentDialogFragment.TAG) }
            R.id.adminSupplyFragment -> { _: View -> {} }
            else -> { _: View -> {} }
        }
    }

    private fun getImageIdForFabOnFragment(fragmentId: Int) : Int {
        return when (fragmentId) {
            R.id.adminResidentsFragment -> R.drawable.ic_baseline_person_add_24
            R.id.adminSupplyFragment -> R.drawable.ic_outline_add_24
            else -> R.drawable.ic_outline_add_24
        }
    }



    private fun getFragmentIdForMenuItemId(menuItemId: Int): Int {
        return when (menuItemId) {
            R.id.admin_overview_menu_item -> R.id.adminOverviewFragment
            R.id.admin_residents_menu_item -> R.id.adminResidentsFragment
            R.id.admin_supply_menu_item -> R.id.adminSupplyFragment
            else -> R.id.adminOverviewFragment
        }
    }
}