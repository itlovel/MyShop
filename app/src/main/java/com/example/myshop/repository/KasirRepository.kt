package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.Kas
import com.example.myshop.model.KeranjangItem
import com.example.myshop.model.Pelanggan
import com.example.myshop.model.PenjualanInsert
import com.example.myshop.model.PenjualanItemInsert
import com.example.myshop.model.PenjualanResponse
import com.example.myshop.model.Produk
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

private const val TAG = "KasirRepository"

class KasirRepository {

    private val db = SupabaseClientProvider.client.postgrest

    suspend fun getProdukAktif(): List<Produk> {
        val result = db.from("produk")
            .select { filter { eq("is_active", true) } }
            .decodeList<Produk>()
        Log.d(TAG, "Produk loaded: ${result.size} items")
        return result
    }

    suspend fun getPelangganAktif(): List<Pelanggan> {
        val result = db.from("pelanggan")
            .select { filter { eq("is_active", true) } }
            .decodeList<Pelanggan>()
        Log.d(TAG, "Pelanggan loaded: ${result.size} items")
        return result
    }

    suspend fun getKasAktif(): List<Kas> {
        val result = db.from("kas")
            .select { filter { eq("is_active", true) } }
            .decodeList<Kas>()
        Log.d(TAG, "Kas loaded: ${result.size} items")
        return result
    }

    // pelangganId non-nullable (wajib diisi sebelum memanggil fungsi ini)
    suspend fun simpanPenjualan(
        pelangganId: String,
        kasId: String,
        keranjang: List<KeranjangItem>,
        jumlahBayar: Double,
    ) {
        val total     = keranjang.sumOf { it.subTotal }
        val kembalian = jumlahBayar - total

        val penjualan: PenjualanResponse = db.from("penjualan")
            .insert(PenjualanInsert(
                pelangganId = pelangganId,
                kasId       = kasId,
                total       = total,
                jumlahBayar = jumlahBayar,
                kembalian   = kembalian,
            )) { select(Columns.list("id")) }
            .decodeSingle()

        Log.d(TAG, "Penjualan created: ${penjualan.id}")

        val items = keranjang.map { item ->
            PenjualanItemInsert(
                penjualanId = penjualan.id,
                produkId    = item.produk.id,
                quantity    = item.quantity.toDouble(),
                hargaSatuan = item.produk.hargaJual,
                subTotal    = item.subTotal,
            )
        }
        db.from("penjualan_item").insert(items)
        Log.d(TAG, "Penjualan items inserted: ${items.size} items")

        // Kurangi stok, ambil nilai terbaru dulu sebelum update
        keranjang.forEach { item ->
            val stokTerbaru = db.from("produk")
                .select { filter { eq("id", item.produk.id) } }
                .decodeSingle<Produk>()
                .stok

            val stokBaru = (stokTerbaru - item.quantity).coerceAtLeast(0.0)
            db.from("produk").update({ set("stok", stokBaru) }) {
                filter { eq("id", item.produk.id) }
            }
            Log.d(TAG, "Stok ${item.produk.nama}: $stokTerbaru → $stokBaru")
        }
    }
}
