package org.example.project.domain.repostories.local

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.local.ConnectedFriends

interface ConnectedFriendsRepo {
    suspend fun insertMember(member: ConnectedFriends): Long
    fun observeFamilyMembers(): Flow<List<ConnectedFriends>>
    suspend fun deleteMember(connectedFriends: ConnectedFriends): Int
}