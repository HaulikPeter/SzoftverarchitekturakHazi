package hu.bme.vik.aut.ui.householdselector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household
import hu.bme.vik.aut.databinding.ActivityHouseHoldSelectorBinding
import hu.bme.vik.aut.databinding.FragmentAdminResidentsBinding
import hu.bme.vik.aut.service.HouseholdsService
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.ui.admindashboard.AdminDashboardActivity
import hu.bme.vik.aut.ui.admindashboard.adapters.ResidentsList.ResidentsListRecyclerViewAdapter
import hu.bme.vik.aut.ui.admindashboard.fragments.AddSupplyDialogFragment
import hu.bme.vik.aut.ui.admindashboard.fragments.AdminSupplyFragment
import hu.bme.vik.aut.ui.householdselector.adapters.householdlist.HouseholdListRecyclerViewAdapter
import hu.bme.vik.aut.ui.householdselector.fragments.AddHouseholdDialogFragment

class HouseHoldSelectorActivity : AppCompatActivity(), HouseholdListRecyclerViewAdapter.HouseholdListRecyclerViewListener, AddHouseholdDialogFragment.AddHouseholdDialogFragmentListener {

    lateinit var binding: ActivityHouseHoldSelectorBinding
    lateinit var householdsRecyclerViewAdapter: HouseholdListRecyclerViewAdapter
    lateinit var loadingProgressBar: ProgressBar
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var adminId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseHoldSelectorBinding.inflate(layoutInflater)
        loadingProgressBar = binding.householdsLoadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE
        floatingActionButton = binding.floatingActionButton

        adminId = FirebaseAuth.getInstance().currentUser!!.uid
        floatingActionButton.setOnClickListener{
            AddHouseholdDialogFragment(this).show(this.supportFragmentManager, AddHouseholdDialogFragment.TAG)
        }

        val householdsRecyclerView = binding.householdsRecyclerView
        householdsRecyclerViewAdapter = HouseholdListRecyclerViewAdapter(this, this)
        householdsRecyclerView.adapter = householdsRecyclerViewAdapter
        householdsRecyclerView.layoutManager = LinearLayoutManager(this)
        loadingProgressBar.visibility = View.VISIBLE
        loadHouseholds()
        setContentView(binding.root)
    }

    private fun loadHouseholds() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        HouseholdsService.getInstance().getHouseholdsForAdmin(adminId, object : OnResultListener<List<Household>>{
            override fun onSuccess(households: List<Household>) {
                householdsRecyclerViewAdapter.setHouseholds(households)
                loadingProgressBar.visibility = View.GONE
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@HouseHoldSelectorActivity, "Error loading households. Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                loadingProgressBar.visibility = View.GONE
            }
        })
    }

    override fun deleteButtonClickedOnHouseholdItem(
        household: Household,
        onResult: (Boolean) -> Unit
    ) {
        HouseholdsService.getInstance().removeHousehold(household.id!!, object: OnResultListener<Boolean> {
            override fun onSuccess(result: Boolean) {
                onResult(result)
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@HouseHoldSelectorActivity, "Error deleting Household. Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                onResult(false)
            }
        })
    }

    override fun householdItemClicked(household: Household) {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.putExtra(AdminDashboardActivity.HOUSEHOLD_ID_ARGUMENT_NAME, household.id)
        startActivity(intent)
    }

    override fun onHouseholdAdded(newHousehold: Household) {
        newHousehold.adminId = adminId
        HouseholdsService.getInstance().addHousehold(newHousehold, object: OnResultListener<String>{
            override fun onSuccess(id: String) {
                newHousehold.id = id
                householdsRecyclerViewAdapter.addHousehold(newHousehold)
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@HouseHoldSelectorActivity, "Error while creating household", Toast.LENGTH_SHORT)
            }
        })
    }
}