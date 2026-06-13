package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.Pelanggan
import com.example.myshop.model.PelangganLog
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val TAG = "PelangganRepository"

interface PelangganRepository {
    suspend fun getPelanggan(): List<Pelanggan>
    suspend fun getActivePelanggan(): List<Pelanggan>
    suspend fun getPelangganById(id: String): Pelanggan
    suspend fun insertPelanggan(nama: String, noTelp: String?)
    suspend fun updatePelanggan(id: String, nama: String, noTelp: String?, isActive: Boolean)
    suspend fun getPelangganLog(pelangganId: String): List<PelangganLog>
}

class SupabasePelangganRepository : PelangganRepository {

    private val db = SupabaseClientProvider.client.postgrest

    override suspend fun getPelanggan(): List<Pelanggan> {
        return db.from("pelanggan")
            .select()
            .decodeList<Pelanggan>()
    }

    override suspend fun getActivePelanggan(): List<Pelanggan> {
        return db.from("pelanggan")
            .select {
                filter {
                    eq("is_active", true)
                }
            }
            .decodeList<Pelanggan>()
    }

    override suspend fun getPelangganById(id: String): Pelanggan {
        val result = db.from("pelanggan")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Pelanggan>()

        Log.d(TAG, "Pelanggan detail loaded: id=$id")
        return result
    }

    override suspend fun insertPelanggan(nama: String, noTelp: String?) {
        db.from("pelanggan").insert(
            PelangganInsert(
                nama = nama.trim(),
                noTelp = noTelp?.trim()?.takeIf { it.isNotEmpty() },
                isActive = true
            )
        )
    }

    override suspend fun updatePelanggan(
        id: String,
        nama: String,
        noTelp: String?,
        isActive: Boolean
    ) {
        db.from("pelanggan")
            .update(
                {
                    set("nama", nama.trim())
                    set("no_telp", noTelp?.trim()?.takeIf { it.isNotEmpty() })
                    set("is_active", isActive)
                }
            ) {
                filter {
                    eq("id", id)
                }
            }
    }

    override suspend fun getPelangganLog(pelangganId: String): List<PelangganLog> {
        return db.from("pelanggan_log")
            .select {
                filter {
                    eq("pelanggan_id", pelangganId)
                }
            }
            .decodeList<PelangganLog>()
            .sortedByDescending { it.createdAt }
    }
}

@Serializable
private data class PelangganInsert(
    @SerialName("nama")
    val nama: String,

    @SerialName("no_telp")
    val noTelp: String?,

    @SerialName("is_active")
    val isActive: Boolean
)