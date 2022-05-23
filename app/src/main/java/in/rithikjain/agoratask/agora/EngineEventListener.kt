package `in`.rithikjain.agoratask.agora

import io.agora.rtm.*

class EngineEventListener : RtmClientListener, RtmCallEventListener {

    private val listeners = mutableListOf<AgoraEventListener>()

    fun registerEventListener(listener: AgoraEventListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeEventListener(listener: AgoraEventListener) {
        listeners.remove(listener)
    }

    override fun onConnectionStateChanged(p0: Int, p1: Int) {}

    override fun onMessageReceived(p0: RtmMessage?, p1: String?) {
        for (listener in listeners) {
            listener.onMessageReceived(p0, p1)
        }
    }

    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {}

    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {}

    override fun onMediaUploadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {}

    override fun onMediaDownloadingProgress(p0: RtmMediaOperationProgress?, p1: Long) {}

    override fun onTokenExpired() {}

    override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {}

    override fun onLocalInvitationReceivedByPeer(p0: LocalInvitation?) {}

    override fun onLocalInvitationAccepted(p0: LocalInvitation?, p1: String?) {
        for (listener in listeners) {
            listener.onLocalInvitationAccepted(p0, p1)
        }
    }

    override fun onLocalInvitationRefused(p0: LocalInvitation?, p1: String?) {
        for (listener in listeners) {
            listener.onLocalInvitationRefused(p0, p1)
        }
    }

    override fun onLocalInvitationCanceled(p0: LocalInvitation?) {}

    override fun onLocalInvitationFailure(p0: LocalInvitation?, p1: Int) {}

    override fun onRemoteInvitationReceived(p0: RemoteInvitation?) {
        for (listener in listeners) {
            listener.onRemoteInvitationReceived(p0)
        }
    }

    override fun onRemoteInvitationAccepted(p0: RemoteInvitation?) {}

    override fun onRemoteInvitationRefused(p0: RemoteInvitation?) {}

    override fun onRemoteInvitationCanceled(p0: RemoteInvitation?) {
        for (listener in listeners) {
            listener.onRemoteInvitationCanceled(p0)
        }
    }

    override fun onRemoteInvitationFailure(p0: RemoteInvitation?, p1: Int) {}
}