package org.example.project.data.remote

import com.example.domain.models.AuthRequest
import org.example.project.domain.models.remote.User
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.domain.models.remote.ConnectRequest
import org.example.project.domain.models.remote.DisconnectRequest

class UserService(private val httpClient: HttpClient) {
    val BaseUrl = "https://family-locator-ktor.onrender.com/"
    val signUpEndPoint = "signUpUser"
    val userExistsEndPoint = "userExists"
    val connectUsersEndPoint = "connectUsers"
    val disconnectUsersEndPoint = "disconnectUser"

    suspend fun signUpUser(user: User): HttpResponse {
        return httpClient.post("$BaseUrl$signUpEndPoint") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    suspend fun checkUserExists(authRequest: AuthRequest): HttpResponse {
        return httpClient.post("$BaseUrl$userExistsEndPoint") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }
    }
    suspend fun connectUsers(connectRequest: ConnectRequest): HttpResponse {
        return httpClient.post("$BaseUrl$connectUsersEndPoint") {
            contentType(ContentType.Application.Json)
            setBody(connectRequest)
        }
    }
    suspend fun disconnectUser(disconnectRequest: DisconnectRequest): HttpResponse {
        return httpClient.post("$BaseUrl$disconnectUsersEndPoint") {
            contentType(ContentType.Application.Json)
            setBody(disconnectRequest)
        }
    }
}