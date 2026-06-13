package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.KasModels
import com.example.myshop.model.Pengeluaran
import com.example.myshop.model.PengeluaranWithKas
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

private const val TAG = "PengeluaranRepository"

@Serializable
private data class PengeluaranInsert(
    @SerialName("id")
    val id: String,
    @SerialName("kas_id")
    val kasId: String,
    @SerialName("tanggal")
    val tanggal: String,
    @SerialName("deskripsi")
    val deskripsi: String,
    @SerialName("total")
    val total: Double,
    @SerialName("status")
    val status: String
)

@Serializable
private data class KasLogInsert(
    @SerialName("kas_id")
    val kasId: String,
    @SerialName("jenis")
    val jenis: String,
    @SerialName("cara_bayar")
    val caraBayar: String,
    @SerialName("nominal")
    val nominal: Double,
    @SerialName("saldo_sebelum")
    val saldoSebelum: Double,
    @SerialName("saldo_sesudah")
    val saldoSesudah: Double,
    @SerialName("keterangan")
    val keterangan: String,
    @SerialName("referensi_id")
    val referensiId: String,
    @SerialName("referensi_tipe")
    val referensiTipe: String,
    @SerialName("tanggal_transaksi")
    val tanggalTransaksi: String
)

class PengeluaranRepository {

    private val db = SupabaseClientProvider.client.postgrest

    suspend fun getPengeluaran(): List<PengeluaranWithKas> {
        val pengeluaran = db.from("pengeluaran")
            .select()
            .decodeList<Pengeluaran>()
            .sortedByDescending { it.tanggal }

        val kas = getSemuaKas()

        return pengeluaran.map { item ->
            PengeluaranWithKas(
                pengeluaran = item,
                namaKas = kas.firstOrNull { it.id == item.kasId }?.namaKas ?: "Kas tidak ditemukan"
            )
        }
    }

    suspend fun getDetailPengeluaran(pengeluaranId: String): PengeluaranWithKas {
        val pengeluaran = db.from("pengeluaran")
            .select {
                filter {
                    eq("id", pengeluaranId)
                }
            }
            .decodeSingle<Pengeluaran>()

        val kas = getSemuaKas()

        return PengeluaranWithKas(
            pengeluaran = pengeluaran,
            namaKas = kas.firstOrNull { it.id == pengeluaran.kasId }?.namaKas ?: "Kas tidak ditemukan"
        )
    }

    suspend fun getKasAktif(): List<KasModels> {
        return getSemuaKas()
            .filter { it.isActive }
            .sortedBy { it.namaKas }
    }

    suspend fun tambahPengeluaran(
        kasId: String,
        tanggal: String,
        deskripsi: String,
        total: Double
    ) {
        val kas = getSemuaKas().firstOrNull { it.id == kasId && it.isActive }
            ?: throw IllegalArgumentException("Kas aktif tidak ditemukan")

        if (total > kas.saldo) {
            throw IllegalArgumentException("Saldo kas tidak mencukupi")
        }

        val pengeluaranId = UUID.randomUUID().toString()
        val saldoSesudah = kas.saldo - total

        val pengeluaranBaru = PengeluaranInsert(
            id = pengeluaranId,
            kasId = kasId,
            tanggal = tanggal,
            deskripsi = deskripsi,
            total = total,
            status = "aktif"
        )

        Log.d(TAG, "Insert pengeluaran payload: ${Json.encodeToString(pengeluaranBaru)}")

        db.from("pengeluaran").insert(pengeluaranBaru)

        db.from("kas").update(
            {
                set("saldo", saldoSesudah)
            }
        ) {
            filter {
                eq("id", kasId)
            }
        }

        db.from("kas_log").insert(
            KasLogInsert(
                kasId = kasId,
                jenis = "KELUAR",
                caraBayar = "debit",
                nominal = total,
                saldoSebelum = kas.saldo,
                saldoSesudah = saldoSesudah,
                keterangan = deskripsi,
                referensiId = pengeluaranId,
                referensiTipe = "PENGELUARAN",
                tanggalTransaksi = tanggal
            )
        )
    }

    private suspend fun getSemuaKas(): List<KasModels> {
        return db.from("kas")
            .select()
            .decodeList<KasModels>()
    }
}
