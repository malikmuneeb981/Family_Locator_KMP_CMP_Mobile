package org.example.project.domain.useCases.remote

class LocationUseCases(
    val connectToUserUseCase: ConnectToUsersWithWebSocketsUseCase,
    val sendLocationUseCase: SendLocationUseCase,
    val observeAllFriendsLatLongsUseCase: ObserveAllFriendsLatLongsUseCase,
    val observeSessionMessagesUseCase: ObserveSessionMessagesUseCase,
    val disconnectUserFromWebSocketsUseCase: DisconnectUserFromWebSocketsUseCase
)