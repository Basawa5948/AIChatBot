package com.example.chatbot.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.chatbot.model.ResponseBody
import com.example.chatbot.utility.StringConstants
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkManager(private val listener:onNetworkResponse) {
    private var gson = Gson()
    var mutableLiveData: MutableLiveData<ResponseBody> = MutableLiveData()
    var mutableLiveErrorData: MutableLiveData<String> = MutableLiveData()
    private val responseList = ArrayList<ResponseBody>()

    fun makeNetworkCall(message:String,senderName:String){
        val requestParameters = fetchQueryMap(message,senderName)
        val apiService = ApiClient().getClient()?.create(ApiInterfaces::class.java)
        val call: Call<ResponseBody>? = apiService?.sendAndReceiveChat(requestParameters)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("APiCall","onFailure with message = ${t.localizedMessage}")
                mutableLiveErrorData.postValue(t.localizedMessage!!)
                listener.onError(t.localizedMessage!!)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("APiCall","onResponse with data = ${response.body()?.getMessageBody()?.getMessage()}")
                mutableLiveData.postValue(response.body())
                listener.onSuccess(response.body()!!)
            }
        })
    }

    private fun fetchQueryMap(message: String, senderName: String): Map<String,String> {
        val queryMap = HashMap<String,String>()
        queryMap[StringConstants.APIKEY] = StringConstants.API_KEY_VALUE
        queryMap[StringConstants.MESSAGE] = message
        when(senderName){
            StringConstants.PERSON_ONE -> {
                queryMap[StringConstants.CHAT_BOT_ID] = StringConstants.CHAT_BOT_ID_ONE
            }
            StringConstants.PERSON_TWO -> {
                queryMap[StringConstants.CHAT_BOT_ID] = StringConstants.CHAT_BOT_ID_TWO
            }
            StringConstants.PERSON_THREE -> {
                queryMap[StringConstants.CHAT_BOT_ID] = StringConstants.CHAT_BOT_ID_THREE
            }
        }
        queryMap[StringConstants.EXTERNAL_ID] = senderName
        return queryMap
    }

    fun getLatestChatData(): MutableLiveData<ResponseBody> {
        return mutableLiveData
    }

    interface onNetworkResponse{
        fun onError(message:String)
        fun onSuccess(response:ResponseBody)
    }
}