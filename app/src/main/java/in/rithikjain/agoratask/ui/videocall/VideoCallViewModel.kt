package `in`.rithikjain.agoratask.ui.videocall

import `in`.rithikjain.agoratask.utils.Constants
import `in`.rithikjain.agoratask.utils.Resource
import `in`.rithikjain.agoratask.utils.toMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class VideoCallViewModel : ViewModel() {

    private var _rtcTokenLiveData = MutableLiveData<Resource<String>>()
    var rtcTokenLiveData: LiveData<Resource<String>> = _rtcTokenLiveData


    fun getRTCToken(channelName: String, username: String) {
        viewModelScope.launch {
            val (_, response, result) = Fuel.get("${Constants.BASE_URL}/rtc/$channelName/publisher/userAccount/$username")
                .awaitStringResponseResult()

            result.fold(
                { data ->
                    val jsonObject = JSONObject(data)
                    val map = jsonObject.toMap()

                    _rtcTokenLiveData.postValue(
                        Resource.Success(
                            map["rtcToken"].toString(),
                            response.statusCode
                        )
                    )
                },
                { error ->
                    _rtcTokenLiveData.postValue(
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