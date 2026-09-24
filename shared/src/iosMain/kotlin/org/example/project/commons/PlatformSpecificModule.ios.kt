package org.example.project.commons

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.ExperimentalForeignApi
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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun platformSpecificModule(): Module {
    return module {
        single<HttpClient> {
            HttpClient(Darwin) {
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
        singleOf(::CreateProfileScreenViewModel)
        singleOf(::SplashViewModel)
        singleOf(::HomeScreenViewModel)
        singleOf(::RealtimeTrackerViewModel)
        singleOf(::AddFriendScreenViewModel)
        singleOf(::ShowMyQRViewModel)
        singleOf(::ScanQRScreenViewModel)


        single<AppUtils> {
            AppUtilsIOS()
        }
        single<NativeLocationService> {
            LocationServiceIOS()
        }
        single<CheckCameraPermIOS> {
            CheckCameraPermIOS()
        }
        single<CheckCameraPermGranted> {
            CheckCameraPermIOS()
        }
        single<CheckLocationStatus>{
            LocationPermissionStatusIOS()
        }
        single<GetNativeLatLongInfo>{
            LocationAddressResolverIOS()
        }
        single<DataStore<Preferences>> {
            createIosDataStore()
        }
        single<GenerateQRFromUserName> {
            GenerateQRFromUserNameIOS()
        }
        single<RoomDatabase.Builder<AppDatabase>> {
            val documentDirectory =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = true,
                    error = null
                )

            val dbPath = requireNotNull(documentDirectory)
                .path
                ?.let {
                    "$it/family_locator.db"
                }
                ?: error("Unable to determine database path")

            Room.databaseBuilder<AppDatabase>(
                name = dbPath
            )
        }

    }
}