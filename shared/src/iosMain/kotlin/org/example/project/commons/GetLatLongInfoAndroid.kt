package org.example.project.commons

import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import kotlin.coroutines.resume

class LocationAddressResolverIOS : GetNativeLatLongInfo {

    override suspend fun getLatLongInfo(
        lat: Double,
        long: Double
    ): String? = suspendCancellableCoroutine { continuation ->

        val geocoder = CLGeocoder()

        val location = CLLocation(
            latitude = lat,
            longitude = long
        )

        geocoder.reverseGeocodeLocation(
            location
        ) { placemarks, error ->

            if (error != null) {
                continuation.resume(null)
                return@reverseGeocodeLocation
            }

            val placemark = placemarks?.firstOrNull()

            if (placemark == null) {
                continuation.resume(null)
                return@reverseGeocodeLocation
            }
            placemark.toString()



            continuation.resume(
                placemark.toString().ifEmpty { null }
            )
        }

        continuation.invokeOnCancellation {
            geocoder.cancelGeocode()
        }
    }
}