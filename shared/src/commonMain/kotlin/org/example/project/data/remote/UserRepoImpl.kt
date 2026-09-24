package org.example.project.data.remote

import com.example.domain.models.AuthRequest
import org.example.project.domain.models.remote.User
import io.ktor.client.statement.HttpResponse
import org.example.project.domain.repostories.remote.UserRepo
import org.example.project.domain.models.remote.ConnectRequest
import org.example.project.domain.models.remote.DisconnectRequest

class UserRepoImpl(private val userService: UserService): UserRepo {

    override suspend fun signUpUser(user: User): HttpResponse {
       return userService.signUpUser(user = user)
    }

    override suspend fun checkUserExists(authRequest: AuthRequest): HttpResponse {
       return userService.checkUserExists(authRequest = authRequest)
    }

    override suspend fun connectUsers(connectRequest: ConnectRequest): HttpResponse {
        return userService.connectUsers(connectRequest = connectRequest)
    }

    override suspend fun disconnectUser(disconnectRequest: DisconnectRequest): HttpResponse {
        return userService.disconnectUser(disconnectRequest = disconnectRequest)
    }


}