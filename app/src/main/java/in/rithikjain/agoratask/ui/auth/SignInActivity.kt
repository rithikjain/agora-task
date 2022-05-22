package `in`.rithikjain.agoratask.ui.auth

import `in`.rithikjain.agoratask.databinding.ActivitySignInBinding
import `in`.rithikjain.agoratask.repository.UserRepository
import `in`.rithikjain.agoratask.ui.home.HomeActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignInActivity"
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var userRepo: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setupListeners()
    }

    override fun onStart() {
        super.onStart()

        // check if user is already signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHomeScreen()
        }
    }

    private fun setupListeners() {
        binding.dontHaveAccountTextView.setOnClickListener {
            navigateToSignUpScreen()
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailTextInputLayout.editText!!.text.toString()
            val password = binding.passwordTextInputLayout.editText!!.text.toString()
            signInUser(email, password)
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    userRepo.getUser(user!!.uid) {
                        userRepo.saveUIDSharedPref(it.uid)
                        userRepo.saveUsernameSharedPref(it.username)

                        navigateToHomeScreen()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToSignUpScreen() {
        finish()
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHomeScreen() {
        finish()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}