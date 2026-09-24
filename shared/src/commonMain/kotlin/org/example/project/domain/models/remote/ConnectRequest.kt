package org.example.project.domain.models.remote

import kotlinx.serialization.Serializable

@Serializable
data class ConnectRequest(
    val myName: String,
    val friendsName: String
)