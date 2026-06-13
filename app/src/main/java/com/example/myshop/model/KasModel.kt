package com.example.myshop.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class KasModels(
    @SerialName("id")
    val id: String,

    @SerialName("nama_kas")
    val namaKas: String,

    @SerialName("saldo")
    val saldo: Double,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String
)