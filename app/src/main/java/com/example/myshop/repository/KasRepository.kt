package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.KasModels
import com.example.myshop.model.KasLogModels
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val TAG = "KasRepository"

@Serializable
data class KasInsert(
    @SerialName("nama_kas")
    val namaKas: String,
    @SerialName("saldo")
    val saldo: Double,
    @SerialName("is_active")
    val isActive: Boolean
)

class KasRepository {

    private val db = SupabaseClientProvider.client.postgrest

    suspend fun ambilSemuaKas(): List<KasModels> {
        val result = db.from("kas")
            .select()
            .decodeList<KasModels>()
        Log.d(TAG, "Kas loaded: ${result.size} items")
        return result
    }

    suspend fun tambahKas(namaKas: String, saldoAwal: Double) {
        val dataKasBaru = KasInsert(
            namaKas = namaKas,
            saldo = saldoAwal,
            isActive = true
        )
        // Menggunakan from("kas").insert(data) untuk dukungan serialisasi yang benar
        db.from("kas").insert(dataKasBaru)
        Log.d(TAG, "Kas created: $namaKas")
    }

    suspend fun ubahStatusKas(kasId: String, statusBaru: Boolean) {
        db.from("kas")
            .update(
                {
                    set("is_active", statusBaru)
                }
            ) {
                filter {
                    eq("id", kasId)
                }
            }
        Log.d(TAG, "Status kas updated: kas_id=$kasId, is_active=$statusBaru")
    }

    suspend fun ambilLogKas(kasId: String): List<KasLogModels> {
        Log.d(TAG, "Mengambil log kas untuk kas_id=$kasId")

        val logs = db.from("kas_log")
            .select {
                filter {
                    eq("kas_id", kasId)
                }
            }
            .decodeList<KasLogModels>()
            .sortedByDescending { it.tanggalTransaksi } // Terbaru di atas untuk riwayat

        Log.d(TAG, "Log kas ditemukan: ${logs.size} item untuk kas_id=$kasId")
        return logs
    }
}
