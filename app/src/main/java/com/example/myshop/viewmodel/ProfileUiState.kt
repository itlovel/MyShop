package com.example.myshop.viewmodel

sealed class ProfileUiState {
    object Idle    : ProfileUiState()
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
