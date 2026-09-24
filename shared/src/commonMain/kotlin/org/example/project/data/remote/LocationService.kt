package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.commons.logger
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.utils.Baseresponse

class LocationService(private val client: HttpClient) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var activeSession: WebSocketSession? = null
    private var activeUser: String? = null
    private var listeningJob: Job? = null

    private val _incomingLocations = MutableSharedFlow<MyLatLng>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun connectToTheUser(
        myUserName: String,
        friendsUserName: String = "all"
    ): Baseresponse<Unit> = mutex.withLock {
        // If already connected for this user and the session is active, reuse it.
        val currentSession = activeSession
        if (activeUser == myUserName && currentSession != null && currentSession.isActive) {
            logger("Loc Service", "Already connected as $myUserName")
            return Baseresponse.Success()
        }

        val receiverParam = if (friendsUserName.isNotBlank()) friendsUserName else "all"
        val webSocketUrl =
            "wss://family-locator-ktor.onrender.com/location-share?sender=$myUserName&receiver=$receiverParam"
        logger("Loc Service", "Connecting to WebSocket as $myUserName")

        return try {
            val session = client.webSocketSession {
                url(webSocketUrl)
            }
            if (session.isActive) {
                activeSession = session
                activeUser = myUserName
                logger("Loc Service", "Connected to WebSocket as $myUserName")

                listeningJob?.cancel()
                listeningJob = scope.launch {
                    try {
                        session.incoming
                            .receiveAsFlow()
                            .filterIsInstance<Frame.Text>()
                            .collect { frame ->
                                val json = frame.readText()
                                logger("Response of Chat", json)
                                try {
                                    val latLng = Json.decodeFromString<MyLatLng>(json)
                                    _incomingLocations.emit(latLng)
                                } catch (e: Exception) {
                                    logger("Loc Service", "JSON parse error: $e")
                                }
                            }
                    } catch (e: Exception) {
                        logger("Loc Service", "Session ended/error: ${e.message}")
                    } finally {
                        if (activeSession === session) {
                            activeSession = null
                        }
                    }
                }

                Baseresponse.Success()
            } else {
                logger("Loc Service", "Error connecting: session inactive")
                Baseresponse.Error("Session inactive")
            }
        } catch (e: Exception) {
            logger("Loc Service", "Error connecting: $e")
            Baseresponse.Error("Error connecting: $e")
        }
    }

    suspend fun sendUserLocation(myLatLng: MyLatLng) {
        try {
            var session = activeSession
            if (session == null || !session.isActive) {
                val sender = myLatLng.senderName
                if (!sender.isNullOrBlank()) {
                    logger("Loc Service", "Session inactive, reconnecting for $sender...")
                    connectToTheUser(myUserName = sender, friendsUserName = "all")
                    session = activeSession
                }
            }
            if (session != null && session.isActive) {
                val encodedLocation = Json.encodeToString(myLatLng)
                session.send(Frame.Text(encodedLocation))
                logger("Loc Service", "Sent location to ${myLatLng.receiverName}")
            } else {
                logger("Loc Service", "webSocket Session not active, could not send to ${myLatLng.receiverName}")
            }
        } catch (e: Exception) {
            logger("Loc Service", "sendUserLocation error: $e")
        }
    }

    fun observeMessages(): Flow<MyLatLng> {
        return _incomingLocations.asSharedFlow()
    }

    fun observeSessionMessages(myUserName: String, friendsUserName: String): Flow<MyLatLng> {
        return _incomingLocations.asSharedFlow().filter { it.senderName == friendsUserName }
    }

    suspend fun closeSession() {
        mutex.withLock {
            try {
                listeningJob?.cancel()
                activeSession?.close()
            } catch (_: Exception) {}
            activeSession = null
            activeUser = null
        }
    }
}