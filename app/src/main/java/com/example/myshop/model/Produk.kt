package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produk(

    @SerialName("id")
    val id: String,

    @SerialName("nama")
    val nama: String,

    @SerialName("harga_beli")
    val hargaBeli: Double,

    @SerialName("harga_jual")
    val hargaJual: Double,

    @SerialName("stok")
    val stok: Double,

    @SerialName("is_active")
    val isActive: Boolean,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)