package org.example.project.commons

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import familylocatorkmp.shared.generated.resources.Res
import familylocatorkmp.shared.generated.resources.app_icon
import familylocatorkmp.shared.generated.resources.ic_avatar
import org.example.project.domain.useCases.remote.SendLocationUseCase
import org.example.project.presentation.screens.realtimetracker.RealtimeTrackerUiEvents
import org.example.project.presentation.screens.realtimetracker.RealtimeTrackerViewModel
import org.example.project.presentation.screens.realtimetracker.SendRealTimeLocationForService
import org.example.project.utils.DataStorePreferences
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

class NativeLocationServiceAndroid(
    private val context: Context
) : NativeLocationService {

    override fun startService() {
        val intent = Intent(
            context,
            LocationServiceAndroid::class.java
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun stopService() {
        val intent = Intent(
            context,
            LocationServiceAndroid::class.java
        )

        context.stopService(intent)
    }
}


class LocationServiceAndroid : Service() {

    var fusedLocationClient: FusedLocationProviderClient ?= null
    var  locationRequest: LocationRequest?=null
    var  locationCallback:LocationCallback ?= null

    val realtimeTrackerViewModel: SendRealTimeLocationForService = getKoin().get<SendRealTimeLocationForService>()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(
                locationResult: LocationResult
            ) {
                locationResult.lastLocation?.let { location ->

                    val latitude = location.latitude
                    val longitude = location.longitude
                    realtimeTrackerViewModel.sendMyLocation(
                        lat = latitude,
                        long = longitude
                    )
                    Log.d(
                        "LocationService",
                        "$latitude, $longitude"
                    )

                    // Send location to your repository/WebSocket
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        {
            val notification = createNotification(this)
            startForeground(145, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        }
        else
        {
            val notification = createNotification(this)
            startForeground(145, notification)
        }

        startLocationUpdates(
            context = this,
            locationRequest = locationRequest,
            locationCallback = locationCallback,
            fusedLocationClient = fusedLocationClient
        )

        return START_STICKY

    }

    override fun onBind(intent: Intent?): IBinder? = null
}
fun startLocationUpdates(context: Context,locationRequest: LocationRequest?,
                         locationCallback:LocationCallback?,
                         fusedLocationClient: FusedLocationProviderClient?): Int {
    return try {

        val fineLocationGranted =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            Log.e("Worker", "Location permission not granted")
            return 0
        }

        val request = locationRequest
            ?: run {
                Log.e("Worker", "LocationRequest is null")
                return 0
            }

        val callback = locationCallback
            ?: run {
                Log.e("Worker", "LocationCallback is null")
                return 0
            }

        @Suppress("MissingPermission")
        fusedLocationClient?.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )

        Log.d("Worker", "Location updates started.")

        1 // success

    } catch (e: Exception) {

        Log.e("Worker", "Failed to start location updates", e)

        0 // failure
    }
}
fun createNotification(service: Service): Notification {

    val channelId = "family_locator_service"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val notificationManager =
            service.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Family Locator",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Family Locator location tracking"
            setShowBadge(false)
        }

        notificationManager.createNotificationChannel(channel)
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(service, channelId)
            .setContentTitle("Family Locator")
            .setContentText("Location tracking is active")
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

    } else {

        @Suppress("DEPRECATION")
        Notification.Builder(service)
            .setContentTitle("Family Locator")
            .setContentText("Location tracking is active")
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setPriority(Notification.PRIORITY_LOW)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }
}