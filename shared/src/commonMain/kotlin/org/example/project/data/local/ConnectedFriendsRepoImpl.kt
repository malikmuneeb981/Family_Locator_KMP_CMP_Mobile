package org.example.project.data.local

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.local.ConnectedFriends
import org.example.project.domain.repostories.local.ConnectedFriendsRepo

class ConnectedFriendsRepoImpl(
    private val dao: ConnectedFriendsDao,
): ConnectedFriendsRepo {
    override suspend fun insertMember(member: ConnectedFriends):Long {
        return dao.insertMember(member = member)
    }

    override fun observeFamilyMembers(): Flow<List<ConnectedFriends>> {
        return dao.observeFamilyMembers()
    }

    override suspend fun deleteMember(connectedFriends: ConnectedFriends): Int {
        return dao.deleteMember(friend = connectedFriends)
    }
}