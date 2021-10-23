package com.example.chatbot.utility

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(val mContext: Context) {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceEditor: SharedPreferences.Editor

    init {
        getSharePreference()
    }

    private fun getSharePreference() {
        sharedPreferences = mContext.getSharedPreferences(StringConstants.SHARED_PREF_FOR_INTRO, Context.MODE_PRIVATE)
    }

    fun saveChatForLater(name: String, chatHistory: String, type: String) {
        when (name) {
            StringConstants.PERSON_ONE -> {
                preferenceEditor = sharedPreferences.edit()
                if (type == StringConstants.SENT) {
                    preferenceEditor.putString(
                        StringConstants.SHARED_PREF_FOR_AMAR_SENT,
                        chatHistory
                    )
                } else {
                    preferenceEditor.putString(
                        StringConstants.SHARED_PREF_FOR_AMAR_RECEIVED,
                        chatHistory
                    )
                }
                preferenceEditor.apply()
            }
            StringConstants.PERSON_TWO -> {
                preferenceEditor = sharedPreferences.edit()
                if (type == StringConstants.SENT) {
                    preferenceEditor.putString(
                        StringConstants.SHARED_PREF_FOR_AKBAR_SENT,
                        chatHistory
                    )
                } else {
                    preferenceEditor.putString(
                        StringConstants.SHARED_PREF_FOR_AKBAR_RECEIVED,
                        chatHistory
                    )
                }
                preferenceEditor.apply()
            }
            StringConstants.PERSON_THREE -> {
                preferenceEditor = sharedPreferences.edit()
                if (type == StringConstants.SENT) {
                    preferenceEditor.putString(
                        StringConstants.SHARED_PREF_FOR_ANTHONY_SENT,
                        chatHistory
                    )
                } else {
                    preferenceEditor.putString(
                        StringConstants.SHARED_PREF_FOR_ANTHONY_RECEIVED,
                        chatHistory
                    )
                }
                preferenceEditor.apply()
            }
        }
    }

    fun getSentChatForLater(name:String):String{
        var offlineChat = ""
        when(name){
            StringConstants.PERSON_ONE -> {
                offlineChat = sharedPreferences.getString(StringConstants.SHARED_PREF_FOR_AMAR_SENT,"").toString()
            }
            StringConstants.PERSON_TWO -> {
                offlineChat = sharedPreferences.getString(StringConstants.SHARED_PREF_FOR_AKBAR_SENT,"").toString()
            }
            StringConstants.PERSON_THREE -> {
                offlineChat = sharedPreferences.getString(StringConstants.SHARED_PREF_FOR_ANTHONY_SENT,"").toString()
            }
        }
        return offlineChat
    }

    fun getReceivedChatForLater(name:String):String{
        var offlineChat = ""
        when(name){
            StringConstants.PERSON_ONE -> {
                offlineChat = sharedPreferences.getString(StringConstants.SHARED_PREF_FOR_AMAR_RECEIVED,"").toString()
            }
            StringConstants.PERSON_TWO -> {
                offlineChat = sharedPreferences.getString(StringConstants.SHARED_PREF_FOR_AKBAR_RECEIVED,"").toString()
            }
            StringConstants.PERSON_THREE -> {
                offlineChat = sharedPreferences.getString(StringConstants.SHARED_PREF_FOR_ANTHONY_RECEIVED,"").toString()
            }
        }
        return offlineChat
    }
}