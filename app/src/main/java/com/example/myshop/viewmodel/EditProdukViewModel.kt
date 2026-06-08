package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.model.Produk
import com.example.myshop.repository.ProdukRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProdukViewModel : ViewModel() {

    private val repository =
        ProdukRepository()

    private val _state =
        MutableStateFlow(
            FormProdukState()
        )

    val state:
            StateFlow<FormProdukState>
            = _state

    private var produkId = ""

    private var stokLama = 0.0

    fun loadProduk(
        produk: Produk
    ) {

        produkId = produk.id

        stokLama = produk.stok

        _state.value =
            _state.value.copy(

                nama = produk.nama,

                hargaBeli =
                    produk.hargaBeli.toString(),

                hargaJual =
                    produk.hargaJual.toString(),

                stok =
                    produk.stok.toString()
            )
    }

    fun updateNama(value: String) {

        _state.value =
            _state.value.copy(
                nama = value
            )
    }

    fun updateHargaBeli(value: String) {

        _state.value =
            _state.value.copy(
                hargaBeli = value
            )
    }

    fun updateHargaJual(value: String) {

        _state.value =
            _state.value.copy(
                hargaJual = value
            )
    }

    fun updateStok(value: String) {

        _state.value =
            _state.value.copy(
                stok = value
            )
    }

    fun simpanProduk() {

        viewModelScope.launch {

            try {

                _state.value =
                    _state.value.copy(
                        isLoading = true
                    )

                val produk = Produk(

                    id = produkId,

                    nama = _state.value.nama,

                    hargaBeli =
                        _state.value.hargaBeli.toDouble(),

                    hargaJual =
                        _state.value.hargaJual.toDouble(),

                    stok =
                        _state.value.stok.toDouble(),

                    isActive = true,

                    createdAt = "",

                    updatedAt = ""
                )

                repository.updateProduk(
                    produk,
                    stokLama
                )

                _state.value =
                    _state.value.copy(
                        success = true,
                        isLoading = false
                    )

            } catch (e: Exception) {

                _state.value =
                    _state.value.copy(

                        error =
                            e.message,

                        isLoading = false
                    )
            }
        }
    }
}