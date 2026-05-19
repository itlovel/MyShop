package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.repository.ProdukRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FormProdukViewModel : ViewModel() {

    private val repository = ProdukRepository()

    private val _state =
        MutableStateFlow(FormProdukState())

    val state: StateFlow<FormProdukState> = _state

    fun updateNama(value: String) {

        _state.value =
            _state.value.copy(nama = value)
    }

    fun updateHargaBeli(value: String) {

        _state.value =
            _state.value.copy(hargaBeli = value)
    }

    fun updateHargaJual(value: String) {

        _state.value =
            _state.value.copy(hargaJual = value)
    }

    fun updateStok(value: String) {

        _state.value =
            _state.value.copy(stok = value)
    }

    fun simpanProduk() {

        val current = _state.value

        if (
            current.nama.isBlank() ||
            current.hargaBeli.isBlank() ||
            current.hargaJual.isBlank() ||
            current.stok.isBlank()
        ) {

            _state.value =
                current.copy(
                    error = "Semua field wajib diisi"
                )

            return
        }

        viewModelScope.launch {

            try {

                _state.value =
                    current.copy(
                        isLoading = true,
                        error = null
                    )

                repository.tambahProduk(

                    nama = current.nama,

                    hargaBeli =
                        current.hargaBeli.toDouble(),

                    hargaJual =
                        current.hargaJual.toDouble(),

                    stok =
                        current.stok.toDouble()
                )

                _state.value =
                    current.copy(
                        success = true,
                        isLoading = false
                    )

            } catch (e: Exception) {

                _state.value =
                    current.copy(
                        isLoading = false,
                        error = e.message
                    )
            }
        }
    }
}