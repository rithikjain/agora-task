package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.databinding.ActivityHomeBinding
import `in`.rithikjain.agoratask.repository.UserRepository
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject
    lateinit var userRepo: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }

        binding.greetingTextView.text = "Hi, ${userRepo.getUsernameSharedPref()}!"
    }

    override fun onStart() {
        super.onStart()

        userRepo.setUserOnline(currentUser.uid)
    }

    override fun onPause() {
        super.onPause()

        userRepo.setUserOffline(currentUser.uid)
    }
}