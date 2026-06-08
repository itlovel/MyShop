package com.example.myshop.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class KasLogModels(
    @SerialName("id")
    val id: String,

    @SerialName("kas_id")
    val kasId: String,

    @SerialName("jenis")
    val jenis: String,

    @SerialName("cara_bayar")
    val caraBayar: String,

    @SerialName("keterangan")
    val keterangan: String? = null,

    @SerialName("nominal")
    val nominal: Double,

    @SerialName("saldo_sebelum")
    val saldoSebelum: Double,

    @SerialName("saldo_sesudah")
    val saldoSesudah: Double,

    @SerialName("referensi_id")
    val referensiId: String? = null,

    @SerialName("referensi_tipe")
    val referensiTipe: String,

    @SerialName("tanggal_transaksi")
    val tanggalTransaksi: String
)
