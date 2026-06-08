package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.UserProfile
import com.example.myshop.model.UserProfileInsert
import com.example.myshop.model.UserProfileWithEmail
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

private const val TAG = "ProfileRepository"

class ProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val db       = supabase.postgrest

    /** Ambil profil milik user yang sedang login */
    suspend fun getProfilSaya(): UserProfileWithEmail {
        val currentUser = supabase.auth.currentUserOrNull()
            ?: error("User belum login")

        val profile = db.from("profiles")
            .select { filter { eq("id", currentUser.id) } }
            .decodeSingle<UserProfile>()

        Log.d(TAG, "Profil dimuat: ${currentUser.email} role=${profile.role}")

        return UserProfileWithEmail(
            profile = profile,
            email   = currentUser.email ?: ""
        )
    }

    /** Ambil semua profil untuk tampilan admin */
    suspend fun getSemuaProfil(): List<UserProfileWithEmail> {
        val result = db.from("profiles")
            .select()
            .decodeList<UserProfile>()
            .sortedWith { a, b ->
                // Admin di atas; dalam grup yang sama urutkan alfabetis by full_name
                if (a.role != b.role) {
                    if (a.isAdmin) -1 else 1
                } else {
                    a.fullName.compareTo(b.fullName, ignoreCase = true)
                }
            }
            .map { UserProfileWithEmail(profile = it, email = "") }

        Log.d(TAG, "Semua profil dimuat: ${result.size} item")
        return result
    }

    /**
     * Daftarkan kasir baru ke Supabase Auth lalu insert ke tabel profiles.
     * Hanya dipanggil dari ProfileViewModel setelah validasi admin.
     */
    suspend fun tambahKasir(fullName: String, email: String, password: String) {
        val response = supabase.auth.signUpWith(Email) {
            this.email    = email
            this.password = password
        }

        val uid = response?.id ?: error("Gagal mendapatkan ID user baru dari Supabase Auth")

        db.from("profiles").insert(
            UserProfileInsert(
                id       = uid,
                fullName = fullName,
                role     = "kasir",
                isActive = true,
            )
        )

        Log.d(TAG, "Kasir baru: $email (uid=$uid)")
    }

    /** Update kolom full_name */
    suspend fun updateNamaProfil(profileId: String, fullNameBaru: String) {
        db.from("profiles").update({ set("full_name", fullNameBaru) }) {
            filter { eq("id", profileId) }
        }
        Log.d(TAG, "full_name diperbarui: profileId=$profileId → \"$fullNameBaru\"")
    }
}
