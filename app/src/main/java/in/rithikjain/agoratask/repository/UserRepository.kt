package `in`.rithikjain.agoratask.repository

import `in`.rithikjain.agoratask.models.User
import android.util.Log
import com.google.firebase.database.DatabaseReference

class UserRepository(private val database: DatabaseReference) {

    fun doesUsernameExist(username: String, onResult: (Boolean) -> Unit) {
        database.child("users").get().addOnSuccessListener {
            for (userSnapshot in it.children) {
                val user = userSnapshot.getValue(User::class.java)
                if (user?.username == username) {
                    Log.d("BRR", user.username)
                    onResult(true)
                    return@addOnSuccessListener
                }
            }
            onResult(false)
        }
    }

    fun saveUser(user: User) {
        database.child("users").child(user.uid ?: "").setValue(user)
    }
}