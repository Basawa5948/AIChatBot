package com.example.chatbot.network

import com.example.chatbot.model.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiInterfaces {
    //https://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&message=Hi&chatBotID=63906&externalID=chirag1
    @GET("?")
    fun sendAndReceiveChat(@QueryMap queryMap: Map<String,String>): Call<ResponseBody>
}