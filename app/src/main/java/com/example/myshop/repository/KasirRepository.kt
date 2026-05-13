package com.example.myshop.repository

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

class KasirRepository {

    private val db = SupabaseClientProvider.client.postgrest

    suspend fun getProdukAktif(): List<Produk> {
        return db.from("produk")
            .select {
                filter { eq("is_active", true) }
            }
            .decodeList()
    }

    suspend fun getPelangganAktif(): List<Pelanggan> {
        return db.from("pelanggan")
            .select {
                filter { eq("is_active", true) }
            }
            .decodeList()
    }

    suspend fun getKasAktif(): List<Kas> {
        return db.from("kas")
            .select {
                filter { eq("is_active", true) }
            }
            .decodeList()
    }

    suspend fun simpanPenjualan(
        pelangganId: String?,
        kasId: String,
        keranjang: List<KeranjangItem>,
        jumlahBayar: Double,
    ) {
        val total = keranjang.sumOf { it.subTotal }
        val kembalian = jumlahBayar - total

        // Insert ke tabel penjualan, ambil id yang baru dibuat
        val penjualan = db.from("penjualan")
            .insert(
                PenjualanInsert(
                    pelangganId = pelangganId,
                    kasId = kasId,
                    total = total,
                    jumlahBayar = jumlahBayar,
                    kembalian = kembalian,
                )
            ) {
                select(Columns.list("id"))
            }
            .decodeSingle<PenjualanResponse>()

        // Insert semua item ke tabel penjualan_item
        val items = keranjang.map { item ->
            PenjualanItemInsert(
                penjualanId = penjualan.id,
                produkId = item.produk.id,
                quantity = item.quantity.toDouble(),
                hargaSatuan = item.produk.hargaJual,
                subTotal = item.subTotal,
            )
        }
        db.from("penjualan_item").insert(items)

        // Kurangi stok setiap produk
        // Supabase tidak support bulk RPC sederhana, jadi update satu per satu
        keranjang.forEach { item ->
            val produkSaatIni = db.from("produk")
                .select { filter { eq("id", item.produk.id) } }
                .decodeSingle<Produk>()

            val stokBaru = produkSaatIni.stok - item.quantity

            db.from("produk").update({
                set("stok", stokBaru)
            }) {
                filter { eq("id", item.produk.id) }
            }
        }
    }
}
