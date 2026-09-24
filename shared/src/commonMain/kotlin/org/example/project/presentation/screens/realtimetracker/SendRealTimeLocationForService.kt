package org.example.project.presentation.screens.realtimetracker

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.example.project.commons.logger
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.domain.models.remote.User
import org.example.project.domain.useCases.local.GetAllFriendsFromRoomUseCase
import org.example.project.domain.useCases.remote.SendLocationUseCase
import org.example.project.utils.DataStorePreferences
import org.example.project.utils.PreferencesKeys

class SendRealTimeLocationForService(
    private val dataStorePreferences: DataStorePreferences,
    private val getAllFriendsFromRoomUseCase: GetAllFriendsFromRoomUseCase,
    private val sendLocationUseCase: SendLocationUseCase,
) {

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    fun sendMyLocation(
        lat: Double,
        long: Double
    ) = coroutineScope.launch {
        val myName = dataStorePreferences
            .getObject<User>(PreferencesKeys.User_Object)
            .firstOrNull()
            ?.userName
            ?: return@launch

        val friends = getAllFriendsFromRoomUseCase.invoke()
            .firstOrNull()
            ?: return@launch

        friends.forEach { friend ->
            logger("Realtime Viewmodel", "Sending loc to ${friend.name} $lat $long")
            sendLocationUseCase(
                myLatLng = MyLatLng(
                    senderName = myName,
                    receiverName = friend.name,
                    latitude = lat,
                    longitude = long
                )
            )
        }
    }
}