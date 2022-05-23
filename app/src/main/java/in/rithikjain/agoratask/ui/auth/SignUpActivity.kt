package `in`.rithikjain.agoratask.ui.auth

import `in`.rithikjain.agoratask.databinding.ActivitySignUpBinding
import `in`.rithikjain.agoratask.models.User
import `in`.rithikjain.agoratask.repository.UserRepository
import `in`.rithikjain.agoratask.ui.home.HomeActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignUpActivity"
    }

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var userRepo: UserRepository

    private var isEmailValid = false
    private var isPasswordValid = false
    private var isUsernameValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setupListeners()
    }

    private fun setupListeners() {
        binding.alreadyHaveAccountTextView.setOnClickListener {
            navigateToSignInScreen()
        }

        binding.usernameTextInputLayout.editText?.doAfterTextChanged {
            if (it.toString().matches(Regex("[a-z]+"))) {
                binding.usernameTextInputLayout.error = null
                isUsernameValid = true
            } else {
                binding.usernameTextInputLayout.error =
                    "Username can only contain lower case alphabets"
                isUsernameValid = false
            }
        }

        binding.emailTextInputLayout.editText?.doAfterTextChanged {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()) {
                binding.emailTextInputLayout.error = null
                isEmailValid = true
            } else {
                binding.emailTextInputLayout.error = "Enter a valid email"
                isEmailValid = false
            }
        }

        binding.passwordTextInputLayout.editText?.doAfterTextChanged {
            if (it.toString().length < 6) {
                binding.passwordTextInputLayout.error = "Password must be at least 5 chars"
                isPasswordValid = false
            } else {
                binding.passwordTextInputLayout.error = null
                isPasswordValid = true
            }
        }

        binding.signUpButton.setOnClickListener {
            val username = binding.usernameTextInputLayout.editText!!.text.toString()
            val email = binding.emailTextInputLayout.editText!!.text.toString()
            val password = binding.passwordTextInputLayout.editText!!.text.toString()

            if (isUsernameValid && isEmailValid && isPasswordValid) {
                userRepo.doesUsernameExist(username) { exists ->
                    if (exists) {
                        Toast.makeText(
                            this,
                            "Username exists, Try using another one :(",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        signUpUser(username, email, password)
                    }
                }
            }
        }
    }

    private fun signUpUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val currUser = auth.currentUser

                    val user = User(currUser!!.uid, username, email, true)
                    userRepo.saveUser(user)
                    userRepo.saveUIDSharedPref(currUser.uid)
                    userRepo.saveUsernameSharedPref(username)

                    navigateToHomeScreen()
                } else {
                    // If sign up fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }

    private fun navigateToHomeScreen() {
        finish()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSignInScreen() {
        finish()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}