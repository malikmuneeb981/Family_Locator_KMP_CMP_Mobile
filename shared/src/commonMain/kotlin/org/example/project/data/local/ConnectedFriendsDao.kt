package org.example.project.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.models.local.ConnectedFriends

@Dao
interface ConnectedFriendsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMember(member: ConnectedFriends): Long

    @Query("SELECT * FROM connectedfriends")
    fun observeFamilyMembers(): Flow<List<ConnectedFriends>>

    @Delete
    suspend fun deleteMember(friend: ConnectedFriends): Int
}