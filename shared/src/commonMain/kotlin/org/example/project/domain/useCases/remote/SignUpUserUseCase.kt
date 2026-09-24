package org.example.project.domain.useCases.remote

import org.example.project.domain.models.remote.User
import io.ktor.client.statement.HttpResponse
import org.example.project.domain.repostories.remote.UserRepo

class SignUpUserUseCase(private val userRepo: UserRepo) {

    suspend operator fun invoke(user: User): HttpResponse = userRepo.signUpUser(user = user)
}