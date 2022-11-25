package hu.bme.vik.aut.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.databinding.ActivityLoggedInUserBinding
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.ui.householdselector.HouseHoldSelectorActivity
import hu.bme.vik.aut.ui.residentDashboard.ResidentDashboardActivity

class LoggedInUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoggedInUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggedInUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnJoinHousehold.setOnClickListener {
            ResidentsService.getInstance().setIsAdminForResident(Firebase.auth.currentUser!!.uid, false, object: OnResultListener<Boolean> {
                override fun onSuccess(result: Boolean) {
                    val intent = Intent(this@LoggedInUserActivity, HouseHoldSelectorActivity::class.java)
                    intent.putExtra(HouseHoldSelectorActivity.IS_ADMIN_PARAMETER_KEY, false)
                    startActivity(intent)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                override fun onError(exception: Exception) {
                    Toast.makeText(this@LoggedInUserActivity, "Error registering user please try again!", Toast.LENGTH_SHORT)
                }
            })
        }
        binding.btnCreateHousehold.setOnClickListener{

            ResidentsService.getInstance().setIsAdminForResident(Firebase.auth.currentUser!!.uid, true, object: OnResultListener<Boolean> {
                override fun onSuccess(result: Boolean) {
                    val intent = Intent(this@LoggedInUserActivity, HouseHoldSelectorActivity::class.java)
                    intent.putExtra(HouseHoldSelectorActivity.IS_ADMIN_PARAMETER_KEY, true)
                    startActivity(intent)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                override fun onError(exception: Exception) {
                    Toast.makeText(this@LoggedInUserActivity, "Error registering admin user please try again!", Toast.LENGTH_SHORT)
                }
            })
        }


        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}