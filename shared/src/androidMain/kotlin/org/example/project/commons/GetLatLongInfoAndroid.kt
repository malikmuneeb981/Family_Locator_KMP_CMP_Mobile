package org.example.project.commons

import android.content.Context
import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class GetLatLongInfoAndroid(private val context: Context): GetNativeLatLongInfo {
    override suspend fun getLatLongInfo(lat: Double, long: Double): String? {

        return try {
            val geocoder = Geocoder(
                context,
                Locale.getDefault()
            )

            val addresses = geocoder.getFromLocation(
                lat,
                long,
                1
            )

            addresses
                ?.firstOrNull()
                ?.getAddressLine(0)

        } catch (e: Exception) {
            null
        }
    }
}