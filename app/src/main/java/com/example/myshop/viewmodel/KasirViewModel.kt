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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

sealed class KasirUiState {
    object Idle    : KasirUiState()
    object Loading : KasirUiState()
    object Success : KasirUiState()
    data class Error(val message: String) : KasirUiState()
}

class KasirViewModel : ViewModel() {

    private val repository = KasirRepository()

    // Data dari Supabase
    private val _produkList    = MutableStateFlow<List<Produk>>(emptyList())
    val produkList: StateFlow<List<Produk>> = _produkList.asStateFlow()

    private val _pelangganList = MutableStateFlow<List<Pelanggan>>(emptyList())
    val pelangganList: StateFlow<List<Pelanggan>> = _pelangganList.asStateFlow()

    private val _kasList = MutableStateFlow<List<Kas>>(emptyList())
    val kasList: StateFlow<List<Kas>> = _kasList.asStateFlow()

    // Pilihan user
    private val _pelangganDipilih = MutableStateFlow<Pelanggan?>(null)
    val pelangganDipilih: StateFlow<Pelanggan?> = _pelangganDipilih.asStateFlow()

    private val _kasDipilih = MutableStateFlow<Kas?>(null)
    val kasDipilih: StateFlow<Kas?> = _kasDipilih.asStateFlow()

    // Keranjang
    private val _keranjang = MutableStateFlow<List<KeranjangItem>>(emptyList())
    val keranjang: StateFlow<List<KeranjangItem>> = _keranjang.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Pembayaran
    private val _jumlahBayar = MutableStateFlow("")
    val jumlahBayar: StateFlow<String> = _jumlahBayar.asStateFlow()

    // State khusus untuk proses loading data awal (tidak mengganggu state transaksi)
    private val _isLoadingData = MutableStateFlow(false)
    val isLoadingData: StateFlow<Boolean> = _isLoadingData.asStateFlow()

    // State untuk proses transaksi (loading, sukses, error)
    private val _uiState = MutableStateFlow<KasirUiState>(KasirUiState.Idle)
    val uiState: StateFlow<KasirUiState> = _uiState.asStateFlow()

    // Visibilitas bottom sheet pelanggan
    private val _showPelangganSheet = MutableStateFlow(false)
    val showPelangganSheet: StateFlow<Boolean> = _showPelangganSheet.asStateFlow()

    // Total belanja sebagai StateFlow agar UI reaktif otomatis
    val totalBelanja: StateFlow<Double> = _keranjang
        .combine(_keranjang) { items, _ -> items.sumOf { it.subTotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    // Kembalian sebagai StateFlow agar UI reaktif otomatis
    val kembalian: StateFlow<Double> = _jumlahBayar
        .combine(totalBelanja) { bayar, total ->
            val bayarNum = bayar.toDoubleOrNull() ?: 0.0
            (bayarNum - total).coerceAtLeast(0.0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    init {
        loadData()
    }

    fun retryLoad() = loadData()

    private fun loadData() {
        viewModelScope.launch {
            _isLoadingData.value = true
            try {
                _produkList.value    = repository.getProdukAktif()
                _pelangganList.value = repository.getPelangganAktif()
                _kasList.value       = repository.getKasAktif()
            } catch (e: Exception) {
                // Tampilkan error muat data — tapi jangan timpa state transaksi
                _uiState.value = KasirUiState.Error("Gagal memuat data: ${e.message}")
            } finally {
                _isLoadingData.value = false
            }
        }
    }

    fun onSearchChange(q: String) { _searchQuery.value = q }

    fun showPilihPelanggan() { _showPelangganSheet.value = true }
    fun hidePilihPelanggan() { _showPelangganSheet.value = false }

    fun pilihPelanggan(p: Pelanggan) {
        _pelangganDipilih.value   = p
        _showPelangganSheet.value = false
    }

    fun hapusPelanggan() { _pelangganDipilih.value = null }

    fun tambahKeKeranjang(produk: Produk) {
        val current = _keranjang.value.toMutableList()
        val idx     = current.indexOfFirst { it.produk.id == produk.id }
        if (idx >= 0) {
            val item = current[idx]
            if (item.quantity < produk.stok.toInt()) {
                current[idx] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (produk.stok > 0) current.add(KeranjangItem(produk = produk, quantity = 1))
        }
        _keranjang.value = current
    }

    fun kurangiDariKeranjang(produkId: String) {
        val current = _keranjang.value.toMutableList()
        val idx     = current.indexOfFirst { it.produk.id == produkId }
        if (idx < 0) return
        val item = current[idx]
        if (item.quantity <= 1) current.removeAt(idx)
        else current[idx] = item.copy(quantity = item.quantity - 1)
        _keranjang.value = current
    }

    fun hapusDariKeranjang(produkId: String) {
        _keranjang.value = _keranjang.value.filter { it.produk.id != produkId }
    }

    fun pilihKas(kas: Kas) { _kasDipilih.value = kas }

    fun onJumlahBayarChange(v: String) {
        if (v.all { it.isDigit() }) _jumlahBayar.value = v
    }

    fun selesaikanTransaksi() {
        val kas       = _kasDipilih.value ?: return
        val keranjang = _keranjang.value
        if (keranjang.isEmpty()) return

        val bayar = _jumlahBayar.value.toDoubleOrNull() ?: 0.0
        if (bayar < totalBelanja.value) {
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
        viewModelScope.launch {
            try { _produkList.value = repository.getProdukAktif() } catch (_: Exception) {}
        }
    }

    fun resetUiState() { _uiState.value = KasirUiState.Idle }
}
