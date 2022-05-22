package `in`.rithikjain.agoratask.agora

import io.agora.rtm.RtmMessage

interface AgoraEventListener {

    fun onMessageReceived(message: RtmMessage?, p1: String?)
}