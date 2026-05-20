package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.repository.ProdukRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProdukViewModel : ViewModel() {

    private val repository = ProdukRepository()

    private val _uiState =
        MutableStateFlow<ProdukUiState>(ProdukUiState.Loading)

    val uiState: StateFlow<ProdukUiState> = _uiState

    init {
        getProduk()
    }

    fun getProduk() {

        viewModelScope.launch {

            try {

                _uiState.value = ProdukUiState.Loading

                val result = repository.getProduk()

                if (result.isEmpty()) {

                    _uiState.value = ProdukUiState.Empty

                } else {

                    _uiState.value =
                        ProdukUiState.Success(result)
                }

            } catch (e: Exception) {

                _uiState.value =
                    ProdukUiState.Error(
                        e.message ?: "Terjadi kesalahan"
                    )
            }
        }
    }
}