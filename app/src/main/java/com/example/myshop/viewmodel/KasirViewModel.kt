package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.model.Kas
import com.example.myshop.model.KeranjangItem
import com.example.myshop.model.Pelanggan
import com.example.myshop.model.Produk
import com.example.myshop.repository.KasirRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class KasirUiState {
    object Idle       : KasirUiState()
    object Loading    : KasirUiState()
    object Success    : KasirUiState()
    data class Error(val message: String) : KasirUiState()
}

class KasirViewModel : ViewModel() {

    private val repository = KasirRepository()

    // Remote data
    private val _produkList    = MutableStateFlow<List<Produk>>(emptyList())
    val produkList: StateFlow<List<Produk>> = _produkList.asStateFlow()

    private val _pelangganList = MutableStateFlow<List<Pelanggan>>(emptyList())
    val pelangganList: StateFlow<List<Pelanggan>> = _pelangganList.asStateFlow()

    private val _kasList       = MutableStateFlow<List<Kas>>(emptyList())
    val kasList: StateFlow<List<Kas>> = _kasList.asStateFlow()

    // Pilihan user ─
    private val _pelangganDipilih = MutableStateFlow<Pelanggan?>(null)
    val pelangganDipilih: StateFlow<Pelanggan?> = _pelangganDipilih.asStateFlow()

    private val _kasDipilih = MutableStateFlow<Kas?>(null)
    val kasDipilih: StateFlow<Kas?> = _kasDipilih.asStateFlow()

    // Keranjang
    private val _keranjang = MutableStateFlow<List<KeranjangItem>>(emptyList())
    val keranjang: StateFlow<List<KeranjangItem>> = _keranjang.asStateFlow()

    //  Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val produkFiltered: StateFlow<List<Produk>>
        get() = _produkList // filtered diproses di UI via derivedStateOf

    // Pembayaran
    private val _jumlahBayar = MutableStateFlow("")
    val jumlahBayar: StateFlow<String> = _jumlahBayar.asStateFlow()

    // UI State
    private val _uiState = MutableStateFlow<KasirUiState>(KasirUiState.Idle)
    val uiState: StateFlow<KasirUiState> = _uiState.asStateFlow()

    //  Dialog
    private val _showPelangganSheet = MutableStateFlow(false)
    val showPelangganSheet: StateFlow<Boolean> = _showPelangganSheet.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = KasirUiState.Loading
            try {
                _produkList.value    = repository.getProdukAktif()
                _pelangganList.value = repository.getPelangganAktif()
                _kasList.value       = repository.getKasAktif()
                _uiState.value       = KasirUiState.Idle
            } catch (e: Exception) {
                _uiState.value = KasirUiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    //  Search
    fun onSearchChange(q: String) { _searchQuery.value = q }

    // Pelanggan
    fun showPilihPelanggan()  { _showPelangganSheet.value = true  }
    fun hidePilihPelanggan()  { _showPelangganSheet.value = false }

    fun pilihPelanggan(p: Pelanggan) {
        _pelangganDipilih.value  = p
        _showPelangganSheet.value = false
    }

    fun hapusPelanggan() { _pelangganDipilih.value = null }

    // Keranjang
    fun tambahKeKeranjang(produk: Produk) {
        val current = _keranjang.value.toMutableList()
        val idx = current.indexOfFirst { it.produk.id == produk.id }
        if (idx >= 0) {
            val item = current[idx]
            // Jangan melebihi stok
            if (item.quantity < produk.stok.toInt()) {
                current[idx] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (produk.stok > 0) {
                current.add(KeranjangItem(produk = produk, quantity = 1))
            }
        }
        _keranjang.value = current
    }

    fun kurangiDariKeranjang(produkId: String) {
        val current = _keranjang.value.toMutableList()
        val idx = current.indexOfFirst { it.produk.id == produkId }
        if (idx < 0) return
        val item = current[idx]
        if (item.quantity <= 1) {
            current.removeAt(idx)
        } else {
            current[idx] = item.copy(quantity = item.quantity - 1)
        }
        _keranjang.value = current
    }

    fun hapusDariKeranjang(produkId: String) {
        _keranjang.value = _keranjang.value.filter { it.produk.id != produkId }
    }

    // Kas & Pembayaran
    fun pilihKas(kas: Kas) { _kasDipilih.value = kas }

    fun onJumlahBayarChange(v: String) {
        if (v.all { it.isDigit() }) _jumlahBayar.value = v
    }

    val totalBelanja: Double
        get() = _keranjang.value.sumOf { it.subTotal }

    val kembalian: Double
        get() {
            val bayar = _jumlahBayar.value.toDoubleOrNull() ?: 0.0
            return (bayar - totalBelanja).coerceAtLeast(0.0)
        }

    // Selesaikan Transaksi
    fun selesaikanTransaksi() {
        val kas      = _kasDipilih.value  ?: return
        val keranjang = _keranjang.value
        if (keranjang.isEmpty()) return

        val bayar = _jumlahBayar.value.toDoubleOrNull() ?: 0.0
        if (bayar < totalBelanja) {
            _uiState.value = KasirUiState.Error("Uang diterima kurang dari total belanja")
            return
        }

        viewModelScope.launch {
            _uiState.value = KasirUiState.Loading
            try {
                repository.simpanPenjualan(
                    pelangganId = _pelangganDipilih.value?.id,
                    kasId       = kas.id,
                    keranjang   = keranjang,
                    jumlahBayar = bayar,
                )
                resetSetelahTransaksi()
                _uiState.value = KasirUiState.Success
            } catch (e: Exception) {
                _uiState.value = KasirUiState.Error(e.message ?: "Transaksi gagal")
            }
        }
    }

    private fun resetSetelahTransaksi() {
        _keranjang.value        = emptyList()
        _pelangganDipilih.value = null
        _kasDipilih.value       = null
        _jumlahBayar.value      = ""
        // Reload produk supaya stok terupdate
        viewModelScope.launch {
            try { _produkList.value = repository.getProdukAktif() } catch (_: Exception) {}
        }
    }

    fun resetUiState() { _uiState.value = KasirUiState.Idle }
}
