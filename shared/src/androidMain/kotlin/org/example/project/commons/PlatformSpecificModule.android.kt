package org.example.project.commons

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.example.project.data.local.AppDatabase
import org.example.project.presentation.screens.addFriend.AddFriendScreenViewModel
import org.example.project.presentation.screens.createProfile.CreateProfileScreenViewModel
import org.example.project.presentation.screens.home.HomeScreenViewModel
import org.example.project.presentation.screens.realtimetracker.RealtimeTrackerViewModel
import org.example.project.presentation.screens.scanQR.ScanQRScreenViewModel
import org.example.project.presentation.screens.showMyQR.ShowMyQRViewModel
import org.example.project.presentation.screens.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual fun platformSpecificModule(): Module {
    return module {
        single<HttpClient> {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    level = LogLevel.ALL
                    logger = object : Logger {
                        override fun log(message: String) {
                            println("KtorHttpClient, $message")
                        }
                    }
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 60_000
                    connectTimeoutMillis = 60_000
                    socketTimeoutMillis = 60_000
                }
                install(WebSockets)
            }
        }
        viewModelOf(::CreateProfileScreenViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::HomeScreenViewModel)
        viewModelOf(::RealtimeTrackerViewModel)
        viewModelOf(::AddFriendScreenViewModel)
        viewModelOf(::ShowMyQRViewModel)
        viewModelOf(::ScanQRScreenViewModel)
        single<DataStore<Preferences>> {
            createAndroidDataStore(get())
        }
        single<CheckCameraPermGranted> {
            CheckCameraPermAndroid(get())
        }
        single<AppUtils> {
            AppUtilsAndroid(get())
        }
        single<NativeLocationService> {
            NativeLocationServiceAndroid(get())
        }
        single<CheckLocationStatus>{
            LocationPermissionStatusAndroid(get())
        }
        single<GetNativeLatLongInfo>{
            GetLatLongInfoAndroid(get())
        }
        single<GenerateQRFromUserName> {
            GenerateQRFromUserNameAndroid()
        }
        single<CheckCameraPermAndroid> {
            CheckCameraPermAndroid(get())
        }
        single<RoomDatabase.Builder<AppDatabase>> {
            val appContext = get<Context>()
            val dbFile = appContext.getDatabasePath(
                "family_locator.db"
            )
            Room.databaseBuilder<AppDatabase>(
                context = appContext,
                name = dbFile.absolutePath
            )
        }
    }
}