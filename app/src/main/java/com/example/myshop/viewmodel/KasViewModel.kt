package com.example.myshop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.model.KasModels
import com.example.myshop.model.KasLogModels
import com.example.myshop.repository.KasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class KasUiState {
    object Idle : KasUiState()
    object Loading : KasUiState()
    object Success : KasUiState()
    data class Error(val message: String) : KasUiState()
}
private const val TAG = "KasViewModel"

class KasViewModel : ViewModel() {
    private val repository = KasRepository()

    // Wadah untuk menyimpan daftar kas yang ditarik dari database
    private val _daftarKas = MutableStateFlow<List<KasModels>>(emptyList())
    val daftarKas: StateFlow<List<KasModels>> = _daftarKas.asStateFlow()

    private val _uiState = MutableStateFlow<KasUiState>(KasUiState.Idle)
    val uiState: StateFlow<KasUiState> = _uiState.asStateFlow()

    // Loading khusus untuk fetch data awal, mengikuti pola KasirViewModel.
    private val _isLoadingData = MutableStateFlow(false)
    val isLoadingData: StateFlow<Boolean> = _isLoadingData.asStateFlow()

    private val _namaKas = MutableStateFlow("")
    val namaKas: StateFlow<String> = _namaKas.asStateFlow()

    private val _saldoAwal = MutableStateFlow("")
    val saldoAwal: StateFlow<String> = _saldoAwal.asStateFlow()

    // State untuk riwayat log kas
    private val _logKas = MutableStateFlow<List<KasLogModels>>(emptyList())
    val logKas: StateFlow<List<KasLogModels>> = _logKas.asStateFlow()

    private val _kasTerpilih = MutableStateFlow<KasModels?>(null)
    val kasTerpilih: StateFlow<KasModels?> = _kasTerpilih.asStateFlow()

    init {
        muatDataKas()
    }

    fun onNamaKasChange(value: String) { _namaKas.value = value }
    fun onSaldoAwalChange(value: String) { _saldoAwal.value = value }

    /**
     * Mengambil data dari repository dan melakukan pengurutan:
     * 1. Status Aktif di atas, Nonaktif di bawah.
     * 2. Di dalam masing-masing grup, urutkan berdasarkan waktu pembuatan (terlama ke terbaru).
     */
    private suspend fun fetchDataAndSort() {
        val hasil = repository.ambilSemuaKas()

        // Melakukan sorting manual agar lebih terkontrol:
        val hasilTerurut = hasil.sortedWith { a, b ->
            // 1. Cek Status dulu: Aktif (true) harus di atas Nonaktif (false)
            if (a.isActive != b.isActive) {
                if (a.isActive) -1 else 1
            } else {
                // 2. Jika statusnya sama, bandingkan waktu dibuatnya
                // String ISO 8601 (2024-xx-xx) akan terurut dari yang terlama (kecil) ke terbaru (besar)
                a.createdAt.compareTo(b.createdAt)
            }
        }

        _daftarKas.value = hasilTerurut
    }

    // Fungsi untuk mengambil data dari repository (untuk UI)
    fun muatDataKas() {
        viewModelScope.launch {
            _isLoadingData.value = true
            try {
                fetchDataAndSort()
                _uiState.value = KasUiState.Idle
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Gagal mengambil data")
            } finally {
                _isLoadingData.value = false
            }
        }
    }

    fun muatDetailKas(kas: KasModels) {
        _kasTerpilih.value = kas
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val logs = repository.ambilLogKas(kas.id)
                _logKas.value = logs
                _uiState.value = KasUiState.Idle
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mengambil riwayat kas untuk kas_id=${kas.id}", e)
                _uiState.value = KasUiState.Error(e.message ?: "Gagal mengambil riwayat kas")
            }
        }
    }

    fun tambahKas() {
        viewModelScope.launch {
            _uiState.value = KasUiState.Loading
            try {
                val saldoDouble = _saldoAwal.value.toDoubleOrNull() ?: 0.0
                repository.tambahKas(_namaKas.value, saldoDouble)
                
                // Refresh list dan tunggu sampai selesai sebelum update status ke Success
                fetchDataAndSort()
                
                _uiState.value = KasUiState.Success
            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun toggleStatusKas(kas: KasModels) {
        viewModelScope.launch {
            try {
                // balik statusnya
                val statusBaru = !kas.isActive
                repository.ubahStatusKas(kas.id, statusBaru)
                
                // Refresh agar urutan langsung berubah di layar
                fetchDataAndSort()

            } catch (e: Exception) {
                _uiState.value = KasUiState.Error(e.message ?: "Gagal mengubah status kas")
            }
        }
    }

    fun resetState() {
        _uiState.value = KasUiState.Idle
        _namaKas.value = ""
        _saldoAwal.value = ""
        _logKas.value = emptyList()
        _kasTerpilih.value = null
    }
}
