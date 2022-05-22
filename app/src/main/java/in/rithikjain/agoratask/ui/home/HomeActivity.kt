package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.databinding.ActivityHomeBinding
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    companion object {
        const val TAG = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private val viewModel: HomeViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }

        initObservers()

        binding.greetingTextView.text = "Hi, ${viewModel.getUsername()}!"
    }

    override fun onStart() {
        super.onStart()

        viewModel.setUserOnline(currentUser.uid)
    }

    override fun onPause() {
        super.onPause()

        viewModel.setUserOffline(currentUser.uid)
    }

    private fun initObservers() {
        viewModel.onlineUsers.observe(this) {
            Log.d(TAG, it.toString())
        }
    }
}