package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.Role
import com.example.myshop.model.UserProfile
import com.example.myshop.model.UserProfileInsert
import com.example.myshop.model.UserProfileWithEmail
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

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

        Log.d(TAG, "Profil dimuat: ${currentUser.email} role=${profile.role} isAdmin=${profile.isAdmin}")

        return UserProfileWithEmail(
            profile = profile,
            email   = currentUser.email ?: ""
        )
    }

    /**
     * Ambil semua profil
     */
    suspend fun getSemuaProfil(): List<UserProfileWithEmail> {
        val result = db.from("profiles")
            .select()
            .decodeList<UserProfile>()
            .sortedWith { a, b ->
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

    /** Daftarkan kasir baru ke Auth lalu insert profil dengan role 'cashier' */
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
                role     = Role.CASHIER,  // nilai yang cocok dengan DB
                isActive = true,
            )
        )

        Log.d(TAG, "Kasir baru berhasil dibuat: $email (uid=$uid)")
    }

    /**
     * Update kolom full_name
     */
    suspend fun updateNamaProfil(profileId: String, fullNameBaru: String) {
        val updated = db.from("profiles")
            .update({ set("full_name", fullNameBaru) }) {
                filter { eq("id", profileId) }
                select(Columns.list("id", "full_name"))
            }
            .decodeList<UserProfile>()

        Log.d(TAG, "Rows updated: ${updated.size} — id=$profileId → \"$fullNameBaru\"")

        if (updated.isEmpty()) {
            error(
                "Update gagal. Kemungkinan penyebab:\n" +
                        "1. Policy UPDATE admin belum dibuat di Supabase.\n" +
                        "2. Role user yang login bukan 'admin' di tabel profiles.\n\n" +
                        "Jalankan SQL ini di Supabase SQL Editor:\n" +
                        "create policy \"profile_update_admin_all\" on public.profiles for update\n" +
                        "to public\n" +
                        "using (get_user_role() = 'admin')\n" +
                        "with check (get_user_role() = 'admin');"
            )
        }
    }
}
