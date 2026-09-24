package org.example.project.domain.useCases.remote

import com.example.domain.models.AuthRequest
import io.ktor.client.statement.HttpResponse
import org.example.project.domain.repostories.remote.UserRepo

class SignAuthUseCase(private val userRepo: UserRepo) {

    suspend operator fun invoke(authRequest: AuthRequest): HttpResponse = userRepo.checkUserExists(authRequest = authRequest)
}