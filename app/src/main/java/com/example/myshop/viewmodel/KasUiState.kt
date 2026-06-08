package com.example.myshop.viewmodel

sealed class KasUiState {
    object Idle : KasUiState()
    object Loading : KasUiState()
    object Success : KasUiState()
    data class Error(val message: String) : KasUiState()
}