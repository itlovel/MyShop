package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.repository.PengeluaranRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PengeluaranViewModel : ViewModel() {

    private val repository = PengeluaranRepository()

    private val _uiState =
        MutableStateFlow<PengeluaranUiState>(PengeluaranUiState.Loading)

    val uiState: StateFlow<PengeluaranUiState> = _uiState

    init {
        getPengeluaran()
    }

    fun getPengeluaran() {
        viewModelScope.launch {
            try {
                _uiState.value = PengeluaranUiState.Loading

                val result = repository.getPengeluaran()

                _uiState.value =
                    if (result.isEmpty()) {
                        PengeluaranUiState.Empty
                    } else {
                        PengeluaranUiState.Success(result)
                    }
            } catch (e: Exception) {
                _uiState.value =
                    PengeluaranUiState.Error(
                        e.message ?: "Gagal mengambil data pengeluaran"
                    )
            }
        }
    }

    fun getDetailPengeluaran(pengeluaranId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = PengeluaranUiState.Loading

                _uiState.value =
                    PengeluaranUiState.DetailSuccess(
                        repository.getDetailPengeluaran(pengeluaranId)
                    )
            } catch (e: Exception) {
                _uiState.value =
                    PengeluaranUiState.Error(
                        e.message ?: "Gagal mengambil detail pengeluaran"
                    )
            }
        }
    }
}
