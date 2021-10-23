package com.example.chatbot

import com.example.chatbot.model.ResponseBody


interface ChatResponseListener {
    fun chatResponseReceived(chat:ResponseBody)
    fun chatResponseFailed(reason:String)
}