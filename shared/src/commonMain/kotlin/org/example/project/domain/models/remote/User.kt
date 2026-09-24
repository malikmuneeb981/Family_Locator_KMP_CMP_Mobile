package org.example.project.domain.models.remote

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userName: String?=null,
    val phoneNumber: String?=null,
    val userImage: String ?= "",
    val groupId: String = ""
)