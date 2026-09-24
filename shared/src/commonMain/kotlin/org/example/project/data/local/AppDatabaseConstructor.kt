package org.example.project.data.local

import androidx.room.RoomDatabaseConstructor

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor :
    RoomDatabaseConstructor<AppDatabase> {

    override fun initialize(): AppDatabase
}