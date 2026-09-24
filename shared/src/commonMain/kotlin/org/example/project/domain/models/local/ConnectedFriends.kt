package org.example.project.domain.models.local


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(
            value = ["name"],
            unique = true
        )
    ]
)
data class ConnectedFriends(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
