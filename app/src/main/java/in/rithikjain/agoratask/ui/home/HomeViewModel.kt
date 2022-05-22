package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.models.User
import `in`.rithikjain.agoratask.repository.UserRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val userRepo: UserRepository): ViewModel() {

    var onlineUsers = MutableLiveData<List<User>>()

    init {
        onlineUsers = userRepo.getOnlineUsers() as MutableLiveData<List<User>>
    }

    fun setUserOnline(uid: String) {
        userRepo.setUserOnline(uid)
    }

    fun setUserOffline(uid: String) {
        userRepo.setUserOffline(uid)
    }

    fun getUsername() = userRepo.getUsernameSharedPref()
}