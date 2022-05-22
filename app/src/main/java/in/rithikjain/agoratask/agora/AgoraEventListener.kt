package `in`.rithikjain.agoratask.agora

import io.agora.rtm.LocalInvitation
import io.agora.rtm.RemoteInvitation
import io.agora.rtm.RtmMessage

interface AgoraEventListener {

    fun onMessageReceived(message: RtmMessage?, p1: String?)

    fun onRemoteInvitationReceived(p0: RemoteInvitation?)

    fun onRemoteInvitationCanceled(p0: RemoteInvitation?)

    fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?)

    fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?)
}