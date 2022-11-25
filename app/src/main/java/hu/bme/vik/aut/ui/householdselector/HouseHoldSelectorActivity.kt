package hu.bme.vik.aut.ui.householdselector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household
import hu.bme.vik.aut.databinding.ActivityHouseHoldSelectorBinding
import hu.bme.vik.aut.service.HouseholdsService
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.ui.admindashboard.AdminDashboardActivity
import hu.bme.vik.aut.ui.householdselector.adapters.householdlist.HouseholdListRecyclerViewAdapter
import hu.bme.vik.aut.ui.householdselector.fragments.AddHouseholdDialogFragment
import hu.bme.vik.aut.ui.login.LoginActivity
import hu.bme.vik.aut.ui.residentDashboard.ResidentDashboardActivity
import kotlin.concurrent.thread

class HouseHoldSelectorActivity() : AppCompatActivity(), HouseholdListRecyclerViewAdapter.HouseholdListRecyclerViewListener, AddHouseholdDialogFragment.AddHouseholdDialogFragmentListener {

    lateinit var binding: ActivityHouseHoldSelectorBinding
    lateinit var householdsRecyclerViewAdapter: HouseholdListRecyclerViewAdapter
    lateinit var loadingProgressBar: ProgressBar
    lateinit var floatingActionButton: FloatingActionButton
    lateinit var userId: String
    var is_admin_household_selector: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseHoldSelectorBinding.inflate(layoutInflater)
        loadingProgressBar = binding.householdsLoadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE
        floatingActionButton = binding.floatingActionButton
        setSupportActionBar(binding.toolbar)
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        is_admin_household_selector = intent.getBooleanExtra(IS_ADMIN_PARAMETER_KEY, false)

        if(is_admin_household_selector) {
            floatingActionButton.show()
            floatingActionButton.setOnClickListener{
                AddHouseholdDialogFragment(this).show(this.supportFragmentManager, AddHouseholdDialogFragment.TAG)
            }
        } else {
            floatingActionButton.hide()
        }


        val householdsRecyclerView = binding.householdsRecyclerView
        householdsRecyclerViewAdapter = HouseholdListRecyclerViewAdapter(this, this, is_admin_household_selector)
        householdsRecyclerView.adapter = householdsRecyclerViewAdapter
        householdsRecyclerView.layoutManager = LinearLayoutManager(this)
        loadingProgressBar.visibility = View.VISIBLE
        loadHouseholds()
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu_actions, menu)

        val item = menu?.findItem(R.id.menu_logout) as MenuItem

        item.setOnMenuItemClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
            true
        }

        return true
    }

    private fun loadHouseholds() {
        thread {
            if (is_admin_household_selector) {
                val adminId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                HouseholdsService.getInstance().getHouseholdsForAdmin(adminId, object : OnResultListener<List<Household>>{
                    override fun onSuccess(result: List<Household>) {
                        runOnUiThread{
                            householdsRecyclerViewAdapter.setHouseholds(result)
                            loadingProgressBar.visibility = View.GONE
                        }
                    }

                    override fun onError(exception: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@HouseHoldSelectorActivity, "Error loading households. Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                            loadingProgressBar.visibility = View.GONE
                        }

                    }
                })
            } else {
                HouseholdsService.getInstance().getAllHouseholds(object : OnResultListener<List<Household>>{
                    override fun onSuccess(result: List<Household>) {
                        runOnUiThread{
                            householdsRecyclerViewAdapter.setHouseholds(result)
                            loadingProgressBar.visibility = View.GONE
                        }
                    }

                    override fun onError(exception: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@HouseHoldSelectorActivity, "Error loading households. Error: ${exception.message}",  Toast.LENGTH_SHORT).show()
                            loadingProgressBar.visibility = View.GONE
                        }
                    }
                })
            }

        }

    }

    override fun deleteButtonClickedOnHouseholdItem(
        household: Household,
        onResult: (Boolean) -> Unit
    ) {
        if(!is_admin_household_selector) {
            onResult(false)
            return
        }
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
        if (is_admin_household_selector) {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            intent.putExtra(AdminDashboardActivity.HOUSEHOLD_ID_ARGUMENT_NAME, household.id)
            startActivity(intent)
        } else {
            ResidentsService.getInstance().setIsHouseholdIDForResident(userId, household.id!!, object: OnResultListener<Boolean> {
                override fun onSuccess(result: Boolean) {
                    if (result) {
                        val intent = Intent(this@HouseHoldSelectorActivity, ResidentDashboardActivity::class.java)
                        intent.putExtra(AdminDashboardActivity.HOUSEHOLD_ID_ARGUMENT_NAME, household.id)
                        startActivity(intent)
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@HouseHoldSelectorActivity, "Error registering resident to household, please try again!", Toast.LENGTH_SHORT)
                    }
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(this@HouseHoldSelectorActivity, "Error registering resident to household, please try again!", Toast.LENGTH_SHORT)
                }
            })

        }
    }

    override fun onHouseholdAdded(newHousehold: Household) {
        if (!is_admin_household_selector) {
            return
        }
        newHousehold.adminId = userId
        HouseholdsService.getInstance().addHousehold(newHousehold, object: OnResultListener<String>{
            override fun onSuccess(result: String) {
                newHousehold.id = result
                householdsRecyclerViewAdapter.addHousehold(newHousehold)
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@HouseHoldSelectorActivity, "Error while creating household", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        val IS_ADMIN_PARAMETER_KEY = "IS_ADMIN_PARAM"
    }
}