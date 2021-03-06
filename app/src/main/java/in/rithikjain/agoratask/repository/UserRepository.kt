package `in`.rithikjain.agoratask.repository

import `in`.rithikjain.agoratask.models.User
import `in`.rithikjain.agoratask.utils.Constants
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.database.DatabaseReference
import `in`.rithikjain.agoratask.utils.PrefHelper.set
import `in`.rithikjain.agoratask.utils.PrefHelper.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserRepository(
    private val database: DatabaseReference,
    private val sharedPref: SharedPreferences
) {

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

    fun getUser(uid: String, onResult: (User) -> Unit) {
        database.child("users").child(uid).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            onResult(user ?: User())
        }
    }

    fun getOnlineUsers(): LiveData<List<User>> {
        val onlineUsers: MutableLiveData<List<User>> = MutableLiveData()
        val currUID = getUIDSharedPref()

        val usersListeners = object : ValueEventListener {
            val users = mutableListOf<User>()

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()

                for (data in dataSnapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user!!.online && user.uid != currUID) users.add(user)
                }

                onlineUsers.postValue(users)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Database", "loadPost:onCancelled $databaseError")
            }
        }
        database.child("users").addValueEventListener(usersListeners)

        return onlineUsers
    }

    fun saveUser(user: User) {
        database.child("users").child(user.uid).setValue(user)
    }

    fun setUserOnline(uid: String) {
        database.child("users").child(uid).child("online").setValue(true)
    }

    fun setUserOffline(uid: String) {
        database.child("users").child(uid).child("online").setValue(false)
        database.child("users").child(uid).child("lastOnline").setValue(System.currentTimeMillis())
    }

    fun saveUsernameSharedPref(username: String) {
        sharedPref[Constants.SHARED_PREF_USERNAME] = username
    }

    fun getUsernameSharedPref(): String {
        return sharedPref[Constants.SHARED_PREF_USERNAME] ?: ""
    }

    fun saveUIDSharedPref(uid: String) {
        sharedPref[Constants.SHARED_PREF_UID] = uid
    }

    fun getUIDSharedPref(): String {
        return sharedPref[Constants.SHARED_PREF_UID] ?: ""
    }
}