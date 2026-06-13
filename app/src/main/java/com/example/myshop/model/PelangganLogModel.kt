package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PelangganLog(
    @SerialName("id")
    val id: String = "",

    @SerialName("pelanggan_id")
    val pelangganId: String = "",

    @SerialName("jenis")
    val jenis: String? = null,

    @SerialName("aksi")
    val aksi: String? = null,

    @SerialName("nama_sebelum")
    val namaSebelum: String? = null,

    @SerialName("nama_sesudah")
    val namaSesudah: String? = null,

    @SerialName("no_telp_sebelum")
    val noTelpSebelum: String? = null,

    @SerialName("no_telp_sesudah")
    val noTelpSesudah: String? = null,

    @SerialName("is_active_sebelum")
    val isActiveSebelum: Boolean? = null,

    @SerialName("is_active_sesudah")
    val isActiveSesudah: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String? = null
) {
    val labelAksi: String
        get() = jenis ?: aksi ?: "PERUBAHAN"
}
