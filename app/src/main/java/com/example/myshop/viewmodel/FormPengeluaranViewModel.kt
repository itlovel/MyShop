package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.repository.PengeluaranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class FormPengeluaranViewModel : ViewModel() {

    private val repository = PengeluaranRepository()

    private val _state =
        MutableStateFlow(
            FormPengeluaranState(
                tanggal = LocalDate.now().toString()
            )
        )

    val state: StateFlow<FormPengeluaranState> = _state

    init {
        getKasAktif()
    }

    fun updateKasId(value: String) {
        _state.value =
            _state.value.copy(kasId = value)
    }

    fun updateTanggal(value: String) {
        _state.value =
            _state.value.copy(tanggal = value)
    }

    fun updateDeskripsi(value: String) {
        _state.value =
            _state.value.copy(deskripsi = value)
    }

    fun updateTotal(value: String) {
        _state.value =
            _state.value.copy(total = value)
    }

    fun simpanPengeluaran() {
        val current = _state.value

        if (
            current.kasId.isBlank() ||
            current.tanggal.isBlank() ||
            current.deskripsi.isBlank() ||
            current.total.isBlank()
        ) {
            _state.value =
                current.copy(
                    error = "Semua field wajib diisi"
                )
            return
        }

        val total = current.total.toDoubleOrNull()

        if (total == null || total <= 0.0) {
            _state.value =
                current.copy(
                    error = "Total pengeluaran tidak valid"
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

                repository.tambahPengeluaran(
                    kasId = current.kasId,
                    tanggal = current.tanggal,
                    deskripsi = current.deskripsi,
                    total = total
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
                        error = e.message ?: "Gagal menyimpan pengeluaran"
                    )
            }
        }
    }

    private fun getKasAktif() {
        viewModelScope.launch {
            try {
                val daftarKas = repository.getKasAktif()

                _state.value =
                    _state.value.copy(
                        daftarKas = daftarKas,
                        kasId = daftarKas.firstOrNull()?.id.orEmpty()
                    )
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(
                        error = e.message ?: "Gagal mengambil data kas"
                    )
            }
        }
    }
}
