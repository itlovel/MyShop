package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.model.Pelanggan
import com.example.myshop.model.PelangganLog
import com.example.myshop.repository.PelangganRepository
import com.example.myshop.repository.SupabasePelangganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class PelangganViewModel(
    private val repository: PelangganRepository = SupabasePelangganRepository()
) : ViewModel() {

    private val _pelangganState =
        MutableStateFlow<UiState<List<Pelanggan>>>(UiState.Loading)
    val pelangganState: StateFlow<UiState<List<Pelanggan>>> =
        _pelangganState.asStateFlow()

    private val _selectedPelangganState =
        MutableStateFlow<UiState<Pelanggan?>>(UiState.Success(null))
    val selectedPelangganState: StateFlow<UiState<Pelanggan?>> =
        _selectedPelangganState.asStateFlow()

    private val _pelangganLogState =
        MutableStateFlow<UiState<List<PelangganLog>>>(UiState.Success(emptyList()))
    val pelangganLogState: StateFlow<UiState<List<PelangganLog>>> =
        _pelangganLogState.asStateFlow()

    private val _formState =
        MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val formState: StateFlow<UiState<Unit>> =
        _formState.asStateFlow()

    init {
        fetchPelanggan()
    }

    fun fetchPelanggan() {
        viewModelScope.launch {
            _pelangganState.value = UiState.Loading
            try {
                _pelangganState.value = UiState.Success(repository.getPelanggan())
            } catch (e: Exception) {
                _pelangganState.value =
                    UiState.Error(e.message ?: "Gagal mengambil data pelanggan")
            }
        }
    }

    fun fetchPelangganById(id: String) {
        if (id.isBlank()) {
            _selectedPelangganState.value = UiState.Error("ID pelanggan tidak valid")
            return
        }

        viewModelScope.launch {
            _selectedPelangganState.value = UiState.Loading
            try {
                _selectedPelangganState.value =
                    UiState.Success(repository.getPelangganById(id))
            } catch (e: Exception) {
                _selectedPelangganState.value =
                    UiState.Error(e.message ?: "Gagal mengambil detail pelanggan")
            }
        }
    }

    fun fetchPelangganLog(id: String) {
        viewModelScope.launch {
            _pelangganLogState.value = UiState.Loading
            try {
                _pelangganLogState.value =
                    UiState.Success(repository.getPelangganLog(id))
            } catch (e: Exception) {
                _pelangganLogState.value =
                    UiState.Error(e.message ?: "Gagal mengambil riwayat pelanggan")
            }
        }
    }

    fun addPelanggan(nama: String, noTelp: String) {
        val cleanNama = nama.trim()
        val cleanNoTelp = noTelp.trim().ifBlank { null }

        if (cleanNama.isEmpty()) {
            _formState.value = UiState.Error("Nama pelanggan tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _formState.value = UiState.Loading
            try {
                repository.insertPelanggan(cleanNama, cleanNoTelp)
                _formState.value = UiState.Success(Unit)
                fetchPelanggan()
            } catch (e: Exception) {
                _formState.value =
                    UiState.Error(e.message ?: "Gagal menambah pelanggan")
            }
        }
    }

    fun editPelanggan(id: String, nama: String, noTelp: String, isActive: Boolean) {
        val cleanNama = nama.trim()
        val cleanNoTelp = noTelp.trim().ifBlank { null }

        if (id.isBlank()) {
            _formState.value = UiState.Error("ID pelanggan tidak valid")
            return
        }

        if (cleanNama.isEmpty()) {
            _formState.value = UiState.Error("Nama pelanggan tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _formState.value = UiState.Loading
            try {
                repository.updatePelanggan(id, cleanNama, cleanNoTelp, isActive)
                _formState.value = UiState.Success(Unit)
                fetchPelanggan()
            } catch (e: Exception) {
                _formState.value =
                    UiState.Error(e.message ?: "Gagal mengubah pelanggan")
            }
        }
    }

    fun resetFormState() {
        _formState.value = UiState.Success(Unit)
    }
}