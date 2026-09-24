package org.example.project.domain.useCases.remote

import org.example.project.domain.models.remote.ConnectRequest
import org.example.project.domain.repostories.remote.LocationRepo
import org.example.project.domain.repostories.remote.UserRepo

class ConnectUsersUseCase(private val userRepo: UserRepo) {
    suspend operator fun invoke(connectRequest: ConnectRequest) = userRepo.connectUsers(
        connectRequest = connectRequest
    )
}