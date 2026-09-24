package org.example.project.domain.useCases.local

import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.domain.repostories.local.ConnectedFriendsRepo

class GetAllFriendsFromRoomUseCase(private val connectedFriendsRepo: ConnectedFriendsRepo) {
    operator fun invoke() = connectedFriendsRepo.observeFamilyMembers()
}