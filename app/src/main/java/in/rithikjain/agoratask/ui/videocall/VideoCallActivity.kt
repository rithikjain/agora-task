package `in`.rithikjain.agoratask.ui.videocall

import `in`.rithikjain.agoratask.R
import `in`.rithikjain.agoratask.databinding.ActivityVideoCallBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
            }
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

        setupListeners()
        setupViews()
        initAndJoinChannel()
    }

    override fun onStop() {
        super.onStop()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun setupListeners() {
        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupViews() {
        binding.remoteUsernameTextView.text = remoteUsername
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