package com.payu.baas.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Base64
import java.io.UnsupportedEncodingException

object Utils {

    internal fun getAbsoluteUrl(relativeUrl: String) = "${BaaSConstants.BS_URL_PREFIX}$relativeUrl"

    internal fun init(context: Context, isProduction: Boolean? = true) {
    }

    fun decodeString(encoded: String): String {
        var decodedString = ""
        decodedString = try {
            val dataDec = Base64.decode(encoded, Base64.DEFAULT)
            String(dataDec, charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            ""
        } finally {
            return decodedString
        }
    }

    fun isInternetAvailable(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}