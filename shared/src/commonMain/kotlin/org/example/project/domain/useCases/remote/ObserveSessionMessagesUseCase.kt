package org.example.project.domain.useCases.remote

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.remote.MyLatLng
import org.example.project.domain.repostories.remote.LocationRepo

class ObserveSessionMessagesUseCase(
    private val locationRepo: LocationRepo
) {
    operator fun invoke(myUserName: String, friendsUserName: String): Flow<MyLatLng> =
        locationRepo.observeSessionMessages(myUserName = myUserName, friendsUserName = friendsUserName)
}
