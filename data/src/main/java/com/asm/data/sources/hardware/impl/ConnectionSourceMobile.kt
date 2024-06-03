package com.asm.data.sources.hardware.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.asm.data.sources.hardware.ConnectionSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ConnectionSourceMobile @Inject constructor(
    @ApplicationContext val context: Context
): ConnectionSource {
    override suspend fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}