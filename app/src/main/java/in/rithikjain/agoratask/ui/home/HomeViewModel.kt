package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.models.User
import `in`.rithikjain.agoratask.repository.UserRepository
import `in`.rithikjain.agoratask.utils.Constants
import `in`.rithikjain.agoratask.utils.Resource
import `in`.rithikjain.agoratask.utils.toMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val userRepo: UserRepository) : ViewModel() {

    var onlineUsers = MutableLiveData<List<User>>()

    private var _rtmTokenLiveData = MutableLiveData<Resource<String>>()
    var rtmTokenLiveData: LiveData<Resource<String>> = _rtmTokenLiveData

    private var isRTMAuthenticated = false

    init {
        onlineUsers = userRepo.getOnlineUsers() as MutableLiveData<List<User>>
    }

    fun setUserOnline(uid: String) {
        if (isRTMAuthenticated) {
            userRepo.setUserOnline(uid)
        }
    }

    fun setUserOffline(uid: String) {
        userRepo.setUserOffline(uid)
    }

    fun getUsername() = userRepo.getUsernameSharedPref()

    fun getRTMToken() {
        viewModelScope.launch {
            val (_, response, result) = Fuel.get("${Constants.BASE_URL}/rtm/${getUsername()}")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val jsonObject = JSONObject(data)
                    val map = jsonObject.toMap()

                    _rtmTokenLiveData.postValue(
                        Resource.Success(
                            map["rtmToken"].toString(),
                            response.statusCode
                        )
                    )

                    isRTMAuthenticated = true
                },
                { error ->
                    _rtmTokenLiveData.postValue(
                        (Resource.Error(
                            error.message ?: "",
                            response.statusCode
                        ))
                    )
                }
            )

        }
    }
}