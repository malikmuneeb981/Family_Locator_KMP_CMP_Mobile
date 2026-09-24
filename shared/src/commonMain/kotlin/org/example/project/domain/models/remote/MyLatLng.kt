package org.example.project.domain.models.remote

import kotlinx.serialization.Serializable

@Serializable
data class MyLatLng(
    val senderName: String? = null,
    val receiverName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)