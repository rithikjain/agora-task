package `in`.rithikjain.agoratask.ui.videocall

import `in`.rithikjain.agoratask.R
import `in`.rithikjain.agoratask.databinding.ActivityVideoCallBinding
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas

class VideoCallActivity : AppCompatActivity() {

    companion object {
        const val TAG = "VideoCallActivity"
    }

    private lateinit var binding: ActivityVideoCallBinding
    private var yourUsername = ""
    private var remoteUsername = ""
    private var channelName = ""

    private var mRtcEngine: RtcEngine? = null

    private var isMicMuted = false
    private var isVideoOff = false

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        // Occurs when a remote user leaves
        override fun onUserOffline(uid: Int, reason: Int) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        if (extras != null) {
            yourUsername = extras.getString("yourUsername") ?: ""
            remoteUsername = extras.getString("remoteUsername") ?: ""
            channelName = extras.getString("channelName") ?: ""
        }

        hideSystemBars()
        setupListeners()
        setupViews()
        initAndJoinChannel()
    }

    override fun onStop() {
        super.onStop()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupListeners() {
        binding.leaveCallButton.setOnClickListener {
            finish()
        }

        binding.muteButton.setOnClickListener {
            if (isMicMuted) toggleYourMic(false) else toggleYourMic(true)
        }

        binding.videoToggleButton.setOnClickListener {
            if (isVideoOff) toggleYourVideo(false) else toggleYourVideo(true)
        }
    }

    private fun setupViews() {
        binding.remoteUsernameTextView.text = remoteUsername
    }

    private fun toggleYourMic(muteMic: Boolean) {
        isMicMuted = if (muteMic) {
            mRtcEngine!!.muteLocalAudioStream(true)
            binding.muteButton.setImageResource(R.drawable.ic_mic_mute)
            true
        } else {
            mRtcEngine!!.muteLocalAudioStream(false)
            binding.muteButton.setImageResource(R.drawable.ic_mic)
            false
        }
    }

    private fun toggleYourVideo(turnVideoOff: Boolean) {
        isVideoOff = if (turnVideoOff) {
            mRtcEngine!!.muteLocalVideoStream(true)
            binding.videoToggleButton.setImageResource(R.drawable.ic_video_off)
            binding.yourVideoContainer.visibility = View.GONE
            true
        } else {
            mRtcEngine!!.muteLocalVideoStream(false)
            binding.videoToggleButton.setImageResource(R.drawable.ic_video)
            binding.yourVideoContainer.visibility = View.VISIBLE
            false
        }
    }

    private fun initAndJoinChannel() {
        try {
            mRtcEngine =
                RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)
        } catch (e: Exception) {
        }

        if (mRtcEngine != null) {
            mRtcEngine!!.enableVideo()

            val yourFrame = RtcEngine.CreateRendererView(baseContext)
            yourFrame.setZOrderOnTop(true)
            binding.yourVideoContainer.addView(yourFrame)
            mRtcEngine!!.setupLocalVideo(VideoCanvas(yourFrame, VideoCanvas.RENDER_MODE_FIT, 0))

            mRtcEngine!!.joinChannel(null, channelName, "", 0)
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        val remoteFrame = RtcEngine.CreateRendererView(baseContext)
        remoteFrame.setZOrderMediaOverlay(true)
        binding.remoteVideoContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }
}