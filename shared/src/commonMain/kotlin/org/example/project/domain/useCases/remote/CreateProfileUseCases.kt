package org.example.project.domain.useCases.remote

class CreateProfileUseCases(
    val signUpUserUseCase: SignUpUserUseCase,
    val signAuthUseCase: SignAuthUseCase
)