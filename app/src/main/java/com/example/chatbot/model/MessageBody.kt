package com.example.chatbot.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class MessageBody {

    @SerializedName("chatBotName")
    @Expose
    private var chatBotName: String? = null

    @SerializedName("chatBotID")
    @Expose
    private var chatBotID: Int? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("emotion")
    @Expose
    private var emotion: String? = null

    fun getChatBotName(): String? {
        return chatBotName
    }

    fun setChatBotName(chatBotName: String?) {
        this.chatBotName = chatBotName
    }

    fun getChatBotID(): Int? {
        return chatBotID
    }

    fun setChatBotID(chatBotID: Int?) {
        this.chatBotID = chatBotID
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getEmotion(): String? {
        return emotion
    }

    fun setEmotion(emotion: String?) {
        this.emotion = emotion
    }
}