package org.example.project.domain.repostories.remote

import com.example.domain.models.AuthRequest
import org.example.project.domain.models.remote.User
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.isActive
import org.example.project.domain.models.remote.ConnectRequest
import org.example.project.domain.models.remote.DisconnectRequest

interface UserRepo {
    suspend fun signUpUser(user: User): HttpResponse
    suspend fun checkUserExists(authRequest: AuthRequest): HttpResponse
    suspend fun connectUsers(connectRequest: ConnectRequest): HttpResponse

    suspend fun disconnectUser(disconnectRequest: DisconnectRequest): HttpResponse
}