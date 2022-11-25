package hu.bme.vik.aut.ui.residentDashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.R
import hu.bme.vik.aut.data.Household
import hu.bme.vik.aut.data.Resident
import hu.bme.vik.aut.data.ResidentStatus
import hu.bme.vik.aut.databinding.ActivityHouseholdUserBinding
import hu.bme.vik.aut.service.HouseholdsService
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.ui.login.LoginActivity

class ResidentDashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityHouseholdUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseholdUserBinding.inflate(layoutInflater)
        setSupportActionBar(binding.residentToolbar)

        ResidentsService.getInstance().getResidentStatus(Firebase.auth.uid!!, residentStatusResult)
        HouseholdsService.getInstance().getHouseholdForResident(Firebase.auth.uid!!, householdResult)
        binding.btnSetStatus.setOnClickListener { setStatus() }

        setContentView(binding.root)
    }

    private val residentStatusResult = object : OnResultListener<ResidentStatus> {
        override fun onSuccess(result: ResidentStatus) {
            when (result) {
                ResidentStatus.HEALTHY -> binding.residentStatusRadioGroup.check(R.id.rbHealthy)
                ResidentStatus.SICK -> binding.residentStatusRadioGroup.check(R.id.rbSick)
            }
        }

        override fun onError(exception: Exception) {
            Toast.makeText(baseContext, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val householdResult = object : OnResultListener<Household> {
        override fun onSuccess(result: Household) {
            binding.residentHouseholdName.text = result.name
        }

        override fun onError(exception: Exception) {
            Toast.makeText(baseContext, exception.message, Toast.LENGTH_SHORT).show()
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
}