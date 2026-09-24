package org.example.project.domain.useCases.remote

import org.example.project.domain.models.remote.DisconnectRequest
import org.example.project.domain.repostories.remote.UserRepo

class DisconnectUserFromWebSocketsUseCase(private val userRepo: UserRepo) {

    suspend operator fun invoke(disconnectRequest: DisconnectRequest) = userRepo.disconnectUser(disconnectRequest =
    disconnectRequest)

}