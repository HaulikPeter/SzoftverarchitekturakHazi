package hu.bme.vik.aut.ui.admindashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.ActivityAdminDashboardBinding
import hu.bme.vik.aut.ui.admindashboard.fragments.*

class AdminDashboardActivity : AppCompatActivity() {

    companion object {
       val HOUSEHOLD_ID_ARGUMENT_NAME = "HOUSEHOLD_ID"
    }

    // code created with help from: https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
    lateinit var binding:  ActivityAdminDashboardBinding
    lateinit var navController: NavController
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var navHostFragment: NavHostFragment
    lateinit var householdId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        val bottomNavigationBar = binding.bottomNavBar
        householdId = intent.getStringExtra(HOUSEHOLD_ID_ARGUMENT_NAME)!!

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.navigate(AdminDashboardLoadingFragmentDirections.actionLoadingHouseholdFragmentToAdminOverviewFragment(householdId))
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
    private fun getFromAdminOvervievFragmentNavigationAction(nextFragmentId: Int): NavDirections? {
        return when(nextFragmentId) {
            R.id.adminResidentsFragment -> AdminOverviewFragmentDirections.actionAdminOverviewFragmentToAdminResidentsFragment(householdId)
            R.id.adminSupplyFragment -> AdminOverviewFragmentDirections.actionAdminOverviewFragmentToAdminSupplyFragment(householdId)
            else -> null
        }
    }

    private fun getFromAdminResidentsFragmentNavigationAction(nextFragmentId: Int): NavDirections? {
        return when(nextFragmentId) {
            R.id.adminOverviewFragment -> AdminResidentsFragmentDirections.actionAdminResidentsFragmentToAdminOverviewFragment(householdId)
            R.id.adminSupplyFragment -> AdminResidentsFragmentDirections.actionAdminResidentsFragmentToAdminSupplyFragment(householdId)
            else -> null
        }
    }

    private fun getFromAdminSupplyFragmentNavigationAction(nextFragmentId: Int): NavDirections? {
        return when(nextFragmentId) {
            R.id.adminOverviewFragment -> AdminSupplyFragmentDirections.actionAdminSupplyFragmentToAdminOverviewFragment(householdId)
            R.id.adminResidentsFragment -> AdminSupplyFragmentDirections.actionAdminSupplyFragmentToAdminResidentsFragment(householdId)
            else -> null
        }
    }
    private fun navigateToFragment(currentFragmentId: Int, nextFragmentId: Int) {
        val action = when (currentFragmentId) {
            R.id.adminOverviewFragment -> getFromAdminOvervievFragmentNavigationAction(nextFragmentId)
            R.id.adminResidentsFragment -> getFromAdminResidentsFragmentNavigationAction(nextFragmentId)
            R.id.adminSupplyFragment -> getFromAdminSupplyFragmentNavigationAction(nextFragmentId)
            else -> null
        }
        if(action == null) {
            return
        }
        floatingActionButton.setOnClickListener(getOnClickListenerForFabOnFragment(nextFragmentId))

        if (getIsFabVisibileOnFragment(nextFragmentId)) {
            floatingActionButton.setImageResource(getImageIdForFabOnFragment(nextFragmentId))
            floatingActionButton.show()
        } else {
            floatingActionButton.visibility = View.GONE
        }
        navController.navigate(action)
    }

    private fun getIsFabVisibileOnFragment(fragmentId: Int) : Boolean {
        return when (fragmentId) {
            R.id.adminOverviewFragment -> false
            R.id.adminResidentsFragment -> false
            R.id.adminSupplyFragment -> true
            else -> false
        }
    }

    private fun getOnClickListenerForFabOnFragment(fragmentId: Int) : (View) -> Unit {
        return when (fragmentId) {
            R.id.adminSupplyFragment -> { _: View ->  AddSupplyDialogFragment(navHostFragment.childFragmentManager.fragments.first() as AdminSupplyFragment).show(this.supportFragmentManager, AddSupplyDialogFragment.TAG)}
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