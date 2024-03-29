package hu.bme.vik.aut.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import hu.bme.vik.aut.R
import hu.bme.vik.aut.databinding.ActivityLoginBinding
import hu.bme.vik.aut.service.OnResultListener
import hu.bme.vik.aut.service.ResidentsService
import hu.bme.vik.aut.ui.householdselector.HouseHoldSelectorActivity
import hu.bme.vik.aut.ui.residentDashboard.ResidentDashboardActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        auth = Firebase.auth
        if (auth.currentUser != null) {
            updateUiWithUser(null)
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView?) {
        val db = FirebaseDatabase.getInstance().reference


        db.child("users").child(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    if (it.child("admin").exists() && it.child("admin").value as Boolean) {
                        intent = Intent(this, HouseHoldSelectorActivity::class.java)
                        intent.putExtra(HouseHoldSelectorActivity.IS_ADMIN_PARAMETER_KEY, true)
                        startActivity(intent)
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else if (it.child("household_id").exists() ) {
                        startActivity(Intent(this, ResidentDashboardActivity::class.java))
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        startActivity(Intent(this, LoggedInUserActivity::class.java))
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                } else {
                    val user = auth.currentUser!!
                    ResidentsService.getInstance().initUserData(user, object: OnResultListener<Boolean> {

                        override fun onSuccess(result: Boolean) {
                            if (result) {
                                startActivity(Intent(this@LoginActivity, LoggedInUserActivity::class.java))
                                setResult(Activity.RESULT_OK)
                                finish()
                            } else {
                                Toast.makeText(this@LoginActivity, "Error registering user, please try again!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(exception: Exception) {
                            Toast.makeText(this@LoginActivity, "Error registering user, please try again!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }.addOnFailureListener {
                showLoginFailed(R.string.login_failed)
            }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        if (loginViewModel.loginResult.value?.desc != null)
            Toast.makeText(applicationContext, loginViewModel.loginResult.value?.desc, Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}