package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produk(
    val id: String = "",
    val nama: String = "",
    @SerialName("harga_jual") val hargaJual: Double = 0.0,
    val stok: Double = 0.0,
    @SerialName("is_active") val isActive: Boolean = true,
)

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

// Payload insert penjualan ketika ada pelanggan dipilih
@Serializable
data class PenjualanDenganPelangganInsert(
    @SerialName("pelanggan_id") val pelangganId: String,
    @SerialName("kas_id") val kasId: String,
    val total: Double,
    @SerialName("jumlah_bayar") val jumlahBayar: Double,
    val kembalian: Double,
)

// Payload insert penjualan tanpa pelanggan (kolom pelanggan_id tidak dikirim) agar Supabase tidak mengisi default auth.uid() yang tidak ada di tabel pelanggan
@Serializable
data class PenjualanTanpaPelangganInsert(
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

// Hanya butuh id setelah insert penjualan berhasil
@Serializable
data class PenjualanResponse(
    val id: String,
)
