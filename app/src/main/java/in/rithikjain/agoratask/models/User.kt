package `in`.rithikjain.agoratask.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val isOnline: Boolean = false
)