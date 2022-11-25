package hu.bme.vik.aut.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

import hu.bme.vik.aut.R
import hu.bme.vik.aut.service.ResidentsService

class LoginViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job

        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    _loginResult.value = LoginResult(
                        success = LoggedInUserView(uid = user.uid, displayName = user.displayName),
                    )
                } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    _loginResult.value = LoginResult(error = R.string.login_failed, desc = task.exception?.message)
                } else if (task.exception is FirebaseTooManyRequestsException) {
                    _loginResult.value = LoginResult(error = R.string.login_failed, desc = task.exception?.message)
                }
                else {
                    registerUser(auth, username, password)
                }
            }
            .addOnFailureListener {
                _loginResult.value = LoginResult(error = R.string.login_failed) }

//        val result = loginRepository.login(username, password)
//
//        if (result is Result.Success) {
//            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
//        } else {
//            _loginResult.value = LoginResult(error = R.string.login_failed)
//        }
    }

    private fun registerUser(auth: FirebaseAuth, username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    _loginResult.value = LoginResult(
                        success = LoggedInUserView(uid = user.uid, displayName = user.displayName))
                } else {
                    _loginResult.value = LoginResult(error = R.string.login_failed, desc = "Failed to register")
                }
            }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}