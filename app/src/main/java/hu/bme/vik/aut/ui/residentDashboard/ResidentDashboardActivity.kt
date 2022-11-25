package hu.bme.vik.aut.ui.residentDashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.vik.aut.databinding.ActivityHouseholdUserBinding

class ResidentDashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityHouseholdUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHouseholdUserBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

}