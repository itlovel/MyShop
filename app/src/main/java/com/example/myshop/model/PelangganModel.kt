package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pelanggan(
    @SerialName("id")
    val id: String = "",

    @SerialName("nama")
    val nama: String = "",

    @SerialName("no_telp")
    val noTelp: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)
