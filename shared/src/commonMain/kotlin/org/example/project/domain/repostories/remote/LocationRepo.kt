package org.example.project.domain.repostories.remote

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.utils.Baseresponse

interface LocationRepo {
    suspend fun connectToTheUser(myUserName: String,
                                 friendsUserName: String): Baseresponse<Unit>
    suspend fun sendUserLocation(myLatLng: MyLatLng)
    fun observeMessages(): Flow<MyLatLng>
    fun observeSessionMessages(myUserName: String, friendsUserName: String): Flow<MyLatLng>
}