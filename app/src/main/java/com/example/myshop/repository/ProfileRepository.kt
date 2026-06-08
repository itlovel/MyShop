package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.UserProfile
import com.example.myshop.model.UserProfileInsert
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

private const val TAG = "ProfileRepository"

class ProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val db       = supabase.postgrest

    /** Ambil profil user yang sedang login */
    suspend fun getProfilSaya(): UserProfile {
        val uid = supabase.auth.currentUserOrNull()?.id
            ?: error("User belum login")

        val result = db.from("profiles")
            .select { filter { eq("id", uid) } }
            .decodeSingle<UserProfile>()

        Log.d(TAG, "Profil dimuat: ${result.email} (${result.role})")
        return result
    }

    /** Ambil semua profil, hanya bisa dipanggil oleh admin (dijaga RLS di Supabase) */
    suspend fun getSemuaProfil(): List<UserProfile> {
        val result = db.from("profiles")
            .select()
            .decodeList<UserProfile>()
            .sortedWith { a, b ->
                // Admin di atas, kasir di bawah; dalam grup yang sama urutkan by nama
                if (a.role != b.role) {
                    if (a.isAdmin) -1 else 1
                } else {
                    a.nama.compareTo(b.nama, ignoreCase = true)
                }
            }

        Log.d(TAG, "Semua profil dimuat: ${result.size} item")
        return result
    }

    /** Buat akun kasir baru.*/
    suspend fun tambahKasir(nama: String, email: String, password: String) {
        val response = supabase.auth.signUpWith(Email) {
            this.email    = email
            this.password = password
        }

        val uid = response?.id ?: error("Gagal mendapatkan ID user baru")

        db.from("profiles").insert(
            UserProfileInsert(
                id    = uid,
                nama  = nama,
                email = email,
                role  = "kasir",
            )
        )

        Log.d(TAG, "Kasir baru dibuat: $email (id=$uid)")
    }

    suspend fun updateNamaProfil(profileId: String, namaBaru: String) {
        db.from("profiles").update({
            set("nama", namaBaru)
        }) {
            filter { eq("id", profileId) }
        }

        Log.d(TAG, "Nama profil diperbarui: profileId=$profileId → $namaBaru")
    }

    /** Cek apakah user yang login adalah admin */
    suspend fun isAdmin(): Boolean {
        return try {
            getProfilSaya().isAdmin
        } catch (e: Exception) {
            false
        }
    }
}
