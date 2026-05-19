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

// Item di keranjang belanja (bukan dari DB, hanya state lokal)
data class KeranjangItem(
    val produk: Produk,
    val quantity: Int,
) {
    val subTotal: Double get() = produk.hargaJual * quantity
}

// Payload yang dikirim ke tabel penjualan
@Serializable
data class PenjualanInsert(
    @SerialName("pelanggan_id") val pelangganId: String? = null,
    @SerialName("kas_id") val kasId: String,
    val total: Double,
    @SerialName("jumlah_bayar") val jumlahBayar: Double,
    val kembalian: Double,
)

// Payload yang dikirim ke tabel penjualan_item
@Serializable
data class PenjualanItemInsert(
    @SerialName("penjualan_id") val penjualanId: String,
    @SerialName("produk_id") val produkId: String,
    val quantity: Double,
    @SerialName("harga_satuan") val hargaSatuan: Double,
    @SerialName("sub_total") val subTotal: Double,
)

// Response setelah insert penjualan (butuh id-nya)
@Serializable
data class PenjualanResponse(
    val id: String,
)
