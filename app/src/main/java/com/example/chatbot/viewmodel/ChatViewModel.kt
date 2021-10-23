package com.example.chatbot.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatbot.ChatResponseListener
import com.example.chatbot.model.ResponseBody
import com.example.chatbot.network.NetworkManager

class ChatViewModel : ViewModel(), NetworkManager.onNetworkResponse {
    private lateinit var chatResponseListener:ChatResponseListener
    private val networkManager = NetworkManager(this)
    var mutableLiveChatData: MutableLiveData<ResponseBody> = MutableLiveData()
    var mutableLiveErrorData: MutableLiveData<String> = MutableLiveData()

    override fun onError(message: String) {
        mutableLiveErrorData.postValue(message)
        chatResponseListener.chatResponseFailed(message)
    }

    override fun onSuccess(response: ResponseBody) {
        mutableLiveChatData = networkManager.getLatestChatData()
        chatResponseListener.chatResponseReceived(response)
    }

    fun initiateConnection(listener: ChatResponseListener,msg:String,name:String) {
        chatResponseListener = listener
        networkManager.makeNetworkCall(msg, name)
    }
}