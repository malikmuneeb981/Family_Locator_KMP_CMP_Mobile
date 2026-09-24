package org.example.project.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.domain.models.local.ConnectedFriends

@Database(
    entities = [
        ConnectedFriends::class
    ],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun connectedFriendsDao(): ConnectedFriendsDao
}