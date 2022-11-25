package hu.bme.vik.aut.ui.residentDashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.ActivityHouseholdUserBinding
import hu.bme.vik.aut.ui.login.LoginActivity

class ResidentDashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityHouseholdUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseholdUserBinding.inflate(layoutInflater)

        setSupportActionBar(binding.residentToolbar)

        loadResidentStatus()
        setContentView(binding.root)
    }

    private fun loadResidentStatus() {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .child(Firebase.auth.currentUser?.uid!!)
            .child("status").get()
            .addOnSuccessListener {
                when (it.value) {
                    "HEALTHY" -> binding.residentStatusRadioGroup.check(R.id.rbHealthy)
                    "SICK" -> binding.residentStatusRadioGroup.check(R.id.rbSick)
                }
            }
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