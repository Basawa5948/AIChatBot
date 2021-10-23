package com.example.chatbot.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class ResponseBody {
    @SerializedName("success")
    @Expose
    private var success: Int? = null

    @SerializedName("errorMessage")
    @Expose
    private var errorMessage: String? = null

    @SerializedName("message")
    @Expose
    private var message: MessageBody? = null

    @SerializedName("data")
    @Expose
    private var data: List<Any?>? = null

    fun getSuccess(): Int? {
        return success
    }

    fun setSuccess(success: Int?) {
        this.success = success
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getMessageBody(): MessageBody? {
        return message
    }

    fun setMessageBody(message: MessageBody?) {
        this.message = message
    }

    fun getData(): List<Any?>? {
        return data
    }

    fun setData(data: List<Any?>?) {
        this.data = data
    }
}