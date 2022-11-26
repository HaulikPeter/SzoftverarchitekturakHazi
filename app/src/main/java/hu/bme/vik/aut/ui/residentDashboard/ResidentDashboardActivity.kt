package hu.bme.vik.aut.ui.residentDashboard

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household
import hu.bme.vik.aut.data.ResidentStatus
import hu.bme.vik.aut.data.Supply
import hu.bme.vik.aut.databinding.ActivityHouseholdUserBinding
import hu.bme.vik.aut.service.HouseholdsService
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.service.SuppliesService
import hu.bme.vik.aut.ui.admindashboard.adapters.supplylist.SupplyConsumptionListRecyclerViewAdapter
import hu.bme.vik.aut.ui.login.LoginActivity
import kotlin.concurrent.thread

class ResidentDashboardActivity : AppCompatActivity(),
    SupplyConsumptionListRecyclerViewAdapter.SupplyConsumptionListRecyclerViewListener {

    lateinit var binding: ActivityHouseholdUserBinding
    lateinit var supplyConsumptionListRecyclerViewAdapter: SupplyConsumptionListRecyclerViewAdapter
    lateinit var userId: String
    lateinit var loadingProgressBar: ProgressBar
    lateinit var household: Household

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseholdUserBinding.inflate(layoutInflater)
        setSupportActionBar(binding.residentToolbar)
        loadingProgressBar = binding.suppliesLoadingProgressBar
        loadingProgressBar.visibility = View.VISIBLE

        userId = Firebase.auth.currentUser!!.uid

        thread {
            binding.residentStatusRadioGroup.isEnabled = false
            ResidentsService.getInstance().getResidentStatus(Firebase.auth.uid!!, residentStatusResult)
        }

        binding.btnSetStatus.setOnClickListener { setStatus() }
        val supplyConsumptionRecyclerView = binding.supplyConsumptionRecyclerView
        supplyConsumptionListRecyclerViewAdapter = SupplyConsumptionListRecyclerViewAdapter(this)
        supplyConsumptionRecyclerView.adapter = supplyConsumptionListRecyclerViewAdapter
        supplyConsumptionRecyclerView.layoutManager = LinearLayoutManager(this)

        thread {
            HouseholdsService.getInstance().getHouseholdForResident(Firebase.auth.uid!!, householdResult)
        }


        setContentView(binding.root)
    }

    private val residentStatusResult = object : OnResultListener<ResidentStatus> {
        override fun onSuccess(result: ResidentStatus) {
            runOnUiThread {
                when (result) {
                    ResidentStatus.HEALTHY -> binding.residentStatusRadioGroup.check(R.id.rbHealthy)
                    ResidentStatus.SICK -> binding.residentStatusRadioGroup.check(R.id.rbSick)
                }
                binding.residentStatusRadioGroup.isEnabled = true
            }
        }

        override fun onError(exception: Exception) {
            Toast.makeText(baseContext, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val householdResult = object : OnResultListener<Household> {
        override fun onSuccess(result: Household) {
            runOnUiThread {
                binding.residentHouseholdName.text = result.name
                household = result
                loadSupplies()
            }
        }

        override fun onError(exception: Exception) {
            Toast.makeText(baseContext, exception.message, Toast.LENGTH_SHORT).show()
            runOnUiThread{
                loadingProgressBar.visibility = View.GONE
            }
        }
    }

    private fun setStatus() {
        val status: ResidentStatus
        when (binding.residentStatusRadioGroup.checkedRadioButtonId) {
            R.id.rbHealthy -> status = ResidentStatus.HEALTHY
            R.id.rbSick -> status = ResidentStatus.SICK
            else -> status = ResidentStatus.HEALTHY
        }
        ResidentsService.getInstance().setResidentStatus(Firebase.auth.uid!!, status, object : OnResultListener<Boolean> {
            override fun onSuccess(result: Boolean) {
                Toast.makeText(baseContext, "Successfully set to $status", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: Exception) {
                Toast.makeText(baseContext, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun loadSupplies() {
        supplyConsumptionListRecyclerViewAdapter.clearSupplies()
        loadingProgressBar.visibility = View.VISIBLE
        thread {
            SuppliesService.getInstance().getSuppliesInHousehold(household.id!!, object :
                OnResultListener<List<Supply>> {
                override fun onSuccess(supplies: List<Supply>) {
                    runOnUiThread{
                        supplyConsumptionListRecyclerViewAdapter.setSupplies(supplies)
                        loadingProgressBar.visibility = View.GONE
                    }
                    for (supply in supplies) {
                        SuppliesService.getInstance().fetchConsumptionForSupply(supply, userId, object: OnResultListener<Supply> {
                            override fun onSuccess(supplyWithConsumption: Supply) {
                                runOnUiThread {
                                    supplyConsumptionListRecyclerViewAdapter.changeSupply(
                                        supply,
                                        supplyWithConsumption
                                    )
                                }
                            }

                            override fun onError(exception: Exception) {
                                supply.consumption = 0
                                runOnUiThread {
                                    supplyConsumptionListRecyclerViewAdapter.changeSupply(
                                        supply,
                                        supply
                                    )
                                }
                            }
                        })
                    }
                }

                override fun onError(exception: Exception) {
                    Toast.makeText(
                        this@ResidentDashboardActivity,
                        "Error loading supplies. Error: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    runOnUiThread {
                        loadingProgressBar.visibility = View.GONE
                    }
                }
            })
        }
    }

    override fun onSupplyConsumptionChangeClicked(
        supply: Supply,
        changeAmount: Long,
        onResult: (Boolean) -> Unit
    ) {
        ResidentsService.getInstance().addConsumption(userId, supply, changeAmount, object: OnResultListener<Boolean> {
            override fun onSuccess(result: Boolean) {
                onResult(result)
            }

            override fun onError(exception: Exception) {
                onResult(false)
            }
        })
    }
}