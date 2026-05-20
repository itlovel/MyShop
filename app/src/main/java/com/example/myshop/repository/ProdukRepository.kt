package com.example.myshop.repository

import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.Produk
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import com.example.myshop.model.InventoryLog
import io.github.jan.supabase.postgrest.from
import java.util.UUID
import kotlinx.serialization.decodeFromString

class ProdukRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getProduk(): List<Produk> {
        val response = supabase
            .from("produk")
            .select() {

                filter {

                    eq(
                        "is_active",
                        true
                    )
                }
            }

        val data = response.data

        return Json.decodeFromString(data)
    }

    suspend fun tambahProduk(

        nama: String,
        hargaBeli: Double,
        hargaJual: Double,
        stok: Double

    ) {

        val produkId = UUID.randomUUID().toString()

        val produk = Produk(

            id = produkId,

            nama = nama,

            hargaBeli = hargaBeli,

            hargaJual = hargaJual,

            stok = stok,

            isActive = true
        )

        supabase
            .from("produk")
            .insert(produk)

        val inventoryLog = InventoryLog(

            produkId = produkId,

            jenis = "MASUK",

            deltaStok = stok,

            stokSebelum = 0.0,

            stokSesudah = stok,

            keterangan = "Stok awal produk"
        )

        supabase
            .from("inventory_log")
            .insert(inventoryLog)
    }

    suspend fun getDetailProduk(

        produkId: String

    ): Produk {

        val response = supabase
            .from("produk")
            .select {

                filter {

                    eq("id", produkId)
                }
            }

        return Json.decodeFromString<List<Produk>>(
            response.data
        ).first()
    }

    suspend fun getInventoryLog(

        produkId: String

    ): List<InventoryLog> {

        val response = supabase
            .from("inventory_log")
            .select {

                filter {

                    eq("produk_id", produkId)
                }
            }

        return Json.decodeFromString(response.data)
    }

    suspend fun updateProduk(

        produk: Produk,

        stokLama: Double

    ) {

        supabase
            .from("produk")
            .update({

                set("nama", produk.nama)
                set("harga_beli", produk.hargaBeli)
                set("harga_jual", produk.hargaJual)
                set("stok", produk.stok)

            }) {

                filter {

                    eq("id", produk.id)
                }
            }

        val deltaStok =
            produk.stok - stokLama

        if (deltaStok != 0.0) {

            supabase
                .from("inventory_log")
                .insert(

                    mapOf(

                        "produk_id" to produk.id,

                        "jenis" to "EDIT",

                        "delta_stok" to deltaStok,

                        "stok_sebelum" to stokLama,

                        "stok_sesudah" to produk.stok,

                        "keterangan" to
                                "Edit stok produk"
                    )
                )
        }
    }

    suspend fun nonaktifkanProduk(
        produkId: String
    ) {

        supabase
            .from("produk")
            .update({

                set(
                    "is_active",
                    false
                )
            }) {

                filter {

                    eq("id", produkId)
                }
            }
    }
}