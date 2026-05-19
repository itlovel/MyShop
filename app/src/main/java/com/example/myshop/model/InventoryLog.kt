package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryLog(

    @SerialName("id")
    val id: String? = null,

    @SerialName("produk_id")
    val produkId: String,

    @SerialName("jenis")
    val jenis: String,

    @SerialName("delta_stok")
    val deltaStok: Double,

    @SerialName("stok_sebelum")
    val stokSebelum: Double,

    @SerialName("stok_sesudah")
    val stokSesudah: Double,

    @SerialName("keterangan")
    val keterangan: String,

    @SerialName("referensi_id")
    val referensiId: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)