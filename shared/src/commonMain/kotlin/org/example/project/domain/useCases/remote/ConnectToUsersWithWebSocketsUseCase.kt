package org.example.project.domain.useCases.remote

import org.example.project.domain.repostories.remote.LocationRepo

class ConnectToUsersWithWebSocketsUseCase(private val locationRepo: LocationRepo) {
    suspend operator fun invoke(myUserName: String,
                                friendsUserName: String) = locationRepo.connectToTheUser(myUserName = myUserName,
                                    friendsUserName = friendsUserName)
}