package hu.bme.vik.aut.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.bme.vik.aut.databinding.ActivityLoggedInUserBinding

class LoggedInUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoggedInUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggedInUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}