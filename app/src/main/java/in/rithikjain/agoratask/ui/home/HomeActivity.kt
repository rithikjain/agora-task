package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.agora.AgoraEventListener
import `in`.rithikjain.agoratask.agora.RTMEventListener
import `in`.rithikjain.agoratask.databinding.ActivityHomeBinding
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtm.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), AgoraEventListener {

    companion object {
        const val TAG = "HomeActivity"
    }

    private lateinit var binding: ActivityHomeBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private val viewModel: HomeViewModel by viewModels()
    private val onlineUsersAdapter = OnlineUsersAdapter()

    @Inject
    lateinit var rtmClient: RtmClient

    @Inject
    lateinit var rtmEventListener: RTMEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }

        rtmClient.login(null, viewModel.getUsername(), null)

        initObservers()
        setupViews()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()

        viewModel.setUserOnline(currentUser.uid)
    }

    override fun onPause() {
        super.onPause()

        viewModel.setUserOffline(currentUser.uid)
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews() {
        binding.greetingTextView.text = "Hi, ${viewModel.getUsername()}!"

        binding.onlineUsersRecyclerView.apply {
            adapter = onlineUsersAdapter
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupListeners() {
        rtmEventListener.registerEventListener(this)
    }

    private fun initObservers() {
        viewModel.onlineUsers.observe(this) {
            Log.d(TAG, it.toString())
            onlineUsersAdapter.updateOnlineUsers(it)
        }
    }

    override fun onMessageReceived(message: RtmMessage?, p1: String?) {
        Log.d(TAG, message?.text.toString())
    }
}