package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pengeluaran(
    @SerialName("id")
    val id: String,

    @SerialName("kas_id")
    val kasId: String,

    @SerialName("tanggal")
    val tanggal: String,

    @SerialName("deskripsi")
    val deskripsi: String,

    @SerialName("total")
    val total: Double,

    @SerialName("status")
    val status: String,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

data class PengeluaranWithKas(
    val pengeluaran: Pengeluaran,
    val namaKas: String
)
