package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.repository.ProdukRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailProdukViewModel : ViewModel() {

    private val repository = ProdukRepository()

    private val _uiState =
        MutableStateFlow<DetailProdukUiState>(
            DetailProdukUiState.Loading
        )

    val uiState:
            StateFlow<DetailProdukUiState>
            = _uiState

    fun getDetailProduk(
        produkId: String
    ) {

        viewModelScope.launch {

            try {

                val produk =
                    repository.getDetailProduk(
                        produkId
                    )

                val logs =
                    repository.getInventoryLog(
                        produkId
                    )

                _uiState.value =
                    DetailProdukUiState.Success(
                        produk,
                        logs
                    )

            } catch (e: Exception) {

                _uiState.value =
                    DetailProdukUiState.Error(
                        e.message ?: "Error"
                    )
            }
        }
    }
}