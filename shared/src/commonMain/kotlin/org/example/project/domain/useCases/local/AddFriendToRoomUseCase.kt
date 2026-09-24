package org.example.project.domain.useCases.local

import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.domain.repostories.local.ConnectedFriendsRepo

class AddFriendToRoomUseCase(private val connectedFriendsRepo: ConnectedFriendsRepo) {
    suspend operator fun invoke(connectedFriends: ConnectedFriends): Long = connectedFriendsRepo.insertMember(
        member = connectedFriends
    )
}