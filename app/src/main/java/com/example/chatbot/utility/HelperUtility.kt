package com.example.chatbot.utility

import android.content.Context
import android.net.ConnectivityManager

class HelperUtility(val mContext: Context) {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}