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

        binding.signUpButton.setOnClickListener {
            //TODO: Add validation for all text fields

            val username = binding.usernameTextInputLayout.editText!!.text.toString()
            val email = binding.emailTextInputLayout.editText!!.text.toString()
            val password = binding.passwordTextInputLayout.editText!!.text.toString()

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

    private fun signUpUser(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val currUser = auth.currentUser

                    val user = User(currUser!!.uid, username, email, false)
                    userRepo.saveUser(user)

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