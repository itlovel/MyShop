package com.example.myshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.model.UserProfile
import com.example.myshop.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    // Profil user yang sedang login
    private val _profilSaya = MutableStateFlow<UserProfile?>(null)
    val profilSaya: StateFlow<UserProfile?> = _profilSaya.asStateFlow()

    // Daftar semua profil (hanya tampil untuk admin)
    private val _daftarProfil = MutableStateFlow<List<UserProfile>>(emptyList())
    val daftarProfil: StateFlow<List<UserProfile>> = _daftarProfil.asStateFlow()

    // State untuk proses loading / aksi
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Form tambah kasir
    private val _formNama     = MutableStateFlow("")
    val formNama: StateFlow<String> = _formNama.asStateFlow()

    private val _formEmail    = MutableStateFlow("")
    val formEmail: StateFlow<String> = _formEmail.asStateFlow()

    private val _formPassword = MutableStateFlow("")
    val formPassword: StateFlow<String> = _formPassword.asStateFlow()

    // Form edit nama
    private val _formNamaEdit = MutableStateFlow("")
    val formNamaEdit: StateFlow<String> = _formNamaEdit.asStateFlow()

    // Profil yang sedang di-edit (admin mengedit profil orang lain)
    private val _profilDiedit = MutableStateFlow<UserProfile?>(null)
    val profilDiedit: StateFlow<UserProfile?> = _profilDiedit.asStateFlow()

    init {
        muatProfilSaya()
    }

    fun muatProfilSaya() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val profil = repository.getProfilSaya()
                _profilSaya.value = profil

                // Kalau admin, langsung muat daftar semua profil
                if (profil.isAdmin) {
                    _daftarProfil.value = repository.getSemuaProfil()
                }

                _uiState.value = ProfileUiState.Idle
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Gagal memuat profil")
            }
        }
    }

    fun muatDaftarProfil() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                _daftarProfil.value = repository.getSemuaProfil()
                _uiState.value = ProfileUiState.Idle
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Gagal memuat daftar profil")
            }
        }
    }

    // Form input tambah kasir
    fun onFormNamaChange(v: String)     { _formNama.value     = v }
    fun onFormEmailChange(v: String)    { _formEmail.value    = v }
    fun onFormPasswordChange(v: String) { _formPassword.value = v }

    fun tambahKasir() {
        val nama     = _formNama.value.trim()
        val email    = _formEmail.value.trim()
        val password = _formPassword.value

        if (nama.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = ProfileUiState.Error("Semua field wajib diisi")
            return
        }
        if (password.length < 6) {
            _uiState.value = ProfileUiState.Error("Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                repository.tambahKasir(nama = nama, email = email, password = password)
                bersihkanFormTambah()
                // Refresh daftar setelah tambah
                _daftarProfil.value = repository.getSemuaProfil()
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Gagal menambah kasir")
            }
        }
    }

    // Edit nama profil
    fun mulaiEditProfil(profil: UserProfile) {
        _profilDiedit.value  = profil
        _formNamaEdit.value  = profil.nama
    }

    fun onFormNamaEditChange(v: String) { _formNamaEdit.value = v }

    fun simpanEditNama() {
        val profil    = _profilDiedit.value ?: return
        val namaBaru  = _formNamaEdit.value.trim()

        if (namaBaru.isBlank()) {
            _uiState.value = ProfileUiState.Error("Nama tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                repository.updateNamaProfil(profil.id, namaBaru)

                // Update state lokal tanpa fetch ulang
                if (_profilSaya.value?.id == profil.id) {
                    _profilSaya.value = _profilSaya.value?.copy(nama = namaBaru)
                }
                _daftarProfil.value = _daftarProfil.value.map {
                    if (it.id == profil.id) it.copy(nama = namaBaru) else it
                }

                _profilDiedit.value = null
                _uiState.value = ProfileUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Gagal menyimpan perubahan")
            }
        }
    }

    fun batalEditProfil() {
        _profilDiedit.value = null
        _formNamaEdit.value = ""
    }

    fun resetUiState() { _uiState.value = ProfileUiState.Idle }

    private fun bersihkanFormTambah() {
        _formNama.value     = ""
        _formEmail.value    = ""
        _formPassword.value = ""
    }
}