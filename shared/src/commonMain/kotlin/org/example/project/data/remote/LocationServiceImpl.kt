package org.example.project.data.remote

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.domain.repostories.remote.LocationRepo
import org.example.project.utils.Baseresponse

class LocationServiceImpl(private val locationService: LocationService): LocationRepo {
    override suspend fun connectToTheUser(
        myUserName: String,
        friendsUserName: String
    ): Baseresponse<Unit> {
        return locationService.connectToTheUser(
            myUserName = myUserName,
            friendsUserName = friendsUserName
        )
    }

    override suspend fun sendUserLocation(myLatLng: MyLatLng) {
        locationService.sendUserLocation(myLatLng = myLatLng)
    }

    override fun observeMessages(): Flow<MyLatLng> {
        return locationService.observeMessages()
    }

    override fun observeSessionMessages(myUserName: String, friendsUserName: String): Flow<MyLatLng> {
        return locationService.observeSessionMessages(
            myUserName = myUserName,
            friendsUserName = friendsUserName
        )
    }
}