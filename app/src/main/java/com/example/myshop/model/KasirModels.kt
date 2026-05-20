package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.example.myshop.model.Produk

@Serializable
data class Pelanggan(
    val id: String = "",
    val nama: String = "",
    @SerialName("no_telp") val noTelp: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class Kas(
    val id: String = "",
    @SerialName("nama_kas") val namaKas: String = "",
    val saldo: Double = 0.0,
    @SerialName("is_active") val isActive: Boolean = true,
)

// State lokal keranjang, tidak disimpan ke DB
data class KeranjangItem(
    val produk: Produk,
    val quantity: Int,
) {
    val subTotal: Double get() = produk.hargaJual * quantity
}

// Pelanggan wajib, pelangganId tidak nullable
@Serializable
data class PenjualanInsert(
    @SerialName("pelanggan_id") val pelangganId: String,
    @SerialName("kas_id") val kasId: String,
    val total: Double,
    @SerialName("jumlah_bayar") val jumlahBayar: Double,
    val kembalian: Double,
)

@Serializable
data class PenjualanItemInsert(
    @SerialName("penjualan_id") val penjualanId: String,
    @SerialName("produk_id") val produkId: String,
    val quantity: Double,
    @SerialName("harga_satuan") val hargaSatuan: Double,
    @SerialName("sub_total") val subTotal: Double,
)

@Serializable
data class PenjualanResponse(
    val id: String,
)

// Snapshot ringkasan transaksi yang sudah selesai, disimpan terpisah agar nilai tidak hilang ketika ViewModel mereset keranjang
data class RingkasanTransaksi(
    val total    : Double,
    val bayar    : Double,
    val kembalian: Double,
)

