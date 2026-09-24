package org.example.project.domain.models.remote

import kotlinx.serialization.Serializable

@Serializable
data class DisconnectRequest(
    val userName: String
)
