package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.agora.AgoraEventListener
import `in`.rithikjain.agoratask.agora.EngineEventListener
import `in`.rithikjain.agoratask.databinding.ActivityHomeBinding
import `in`.rithikjain.agoratask.ui.auth.SignInActivity
import `in`.rithikjain.agoratask.ui.videocall.VideoCallActivity
import `in`.rithikjain.agoratask.utils.Resource
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var onlineUsersAdapter: OnlineUsersAdapter

    @Inject
    lateinit var rtmClient: RtmClient

    @Inject
    lateinit var engineEventListener: EngineEventListener

    @Inject
    lateinit var rtmCallManager: RtmCallManager

    private var ringingDialog: AlertDialog? = null
    private var receivingCallDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        if (auth.currentUser != null) {
            currentUser = auth.currentUser!!
        }

        startLoading()
        viewModel.getRTMToken()

        initObservers()
        setupViews()
        setupListeners()
        requestPermissions()
    }

    override fun onStart() {
        super.onStart()

        viewModel.setUserOnline(currentUser.uid)
    }

    override fun onPause() {
        super.onPause()

        viewModel.setUserOffline(currentUser.uid)
    }

    private fun startLoading() {
        binding.onlineUsersRecyclerView.visibility = View.GONE
        binding.greetingTextView.visibility = View.GONE
        binding.onlineTextView.visibility = View.GONE
        binding.homeProgressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.homeProgressBar.visibility = View.GONE
        binding.onlineUsersRecyclerView.visibility = View.VISIBLE
        binding.greetingTextView.visibility = View.VISIBLE
        binding.onlineTextView.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews() {
        binding.greetingTextView.text = "Hi, ${viewModel.getUsername()}!"

        onlineUsersAdapter = OnlineUsersAdapter {
            callUser(it.username)
        }

        binding.onlineUsersRecyclerView.apply {
            adapter = onlineUsersAdapter
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupListeners() {
        engineEventListener.registerEventListener(this)

        viewModel.rtmTokenLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    stopLoading()
                    rtmClient.login(it.data.toString(), viewModel.getUsername(), null)
                    viewModel.setUserOnline(currentUser.uid)
                    Log.d(TAG, it.data.toString())
                }
                is Resource.Error -> {
                    stopLoading()
                    Log.d(TAG, it.message.toString())
                }
            }
        }

        binding.agoraLogoImageView.setOnLongClickListener {
            logOut()
            return@setOnLongClickListener true
        }
    }

    private fun initObservers() {
        viewModel.onlineUsers.observe(this) {
            Log.d(TAG, it.toString())
            onlineUsersAdapter.updateOnlineUsers(it)
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_SHORT).show()
        finish()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun requestPermissions() {
        permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).build()
            .send {

            }
    }

    private fun startVideoCall(yourUsername: String, remoteUsername: String, channelName: String) {
        val intent = Intent(this, VideoCallActivity::class.java)
        intent.putExtra("yourUsername", yourUsername)
        intent.putExtra("remoteUsername", remoteUsername)
        intent.putExtra("channelName", channelName)

        startActivity(intent)
    }

    private fun callUser(username: String) {
        val invitation = rtmCallManager.createLocalInvitation(username)
        invitation.channelId = username

        rtmCallManager.sendLocalInvitation(invitation, null)

        ringingDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Ringing $username...")
            .setNegativeButton("Cancel") { dialog, _ ->
                rtmCallManager.cancelLocalInvitation(invitation, null)
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    override fun onMessageReceived(message: RtmMessage?, p1: String?) {
        Log.d(TAG, message?.text.toString())
    }

    override fun onRemoteInvitationReceived(p0: RemoteInvitation?) {
        runOnUiThread {
            receivingCallDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Getting a call from ${p0?.callerId}...")
                .setNegativeButton("Decline") { dialog, _ ->
                    rtmCallManager.refuseRemoteInvitation(p0, null)
                    dialog.dismiss()
                }
                .setPositiveButton("Answer") { dialog, _ ->
                    rtmCallManager.acceptRemoteInvitation(p0, null)
                    dialog.dismiss()

                    startVideoCall(viewModel.getUsername(), p0!!.callerId, p0.channelId)
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onRemoteInvitationCanceled(p0: RemoteInvitation?) {
        runOnUiThread {
            receivingCallDialog?.dismiss()
            Toast.makeText(this, "Call Invitation Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {
        runOnUiThread {
            ringingDialog?.dismiss()
            Toast.makeText(this, "Call Accepted", Toast.LENGTH_SHORT).show()

            startVideoCall(viewModel.getUsername(), p0!!.calleeId, p0.channelId)
        }
    }

    override fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?) {
        runOnUiThread {
            ringingDialog?.dismiss()
            Toast.makeText(this, "Call Declined", Toast.LENGTH_SHORT).show()
        }
    }
}