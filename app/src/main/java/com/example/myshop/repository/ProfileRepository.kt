package com.example.myshop.repository

import android.util.Log
import com.example.myshop.data.SupabaseClientProvider
import com.example.myshop.model.Role
import com.example.myshop.model.UserProfile
import com.example.myshop.model.UserProfileWithEmail
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

private const val TAG = "ProfileRepository"

class ProfileRepository {

    private val supabase = SupabaseClientProvider.client
    private val db       = supabase.postgrest

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

    /**
     * Daftarkan kasir baru.
     */
    suspend fun tambahKasir(
        fullName     : String,
        email        : String,
        password     : String,
        adminEmail   : String,
        adminPassword: String,
    ) {
        // 1. Register kasir baru (session aktif BERPINDAH ke kasir baru setelah ini)
        val response = supabase.auth.signUpWith(Email) {
            this.email    = email
            this.password = password
        }
        val uid = response?.id ?: error("Gagal mendapatkan ID user baru")
        Log.d(TAG, "Kasir baru terdaftar di Auth: $email (uid=$uid)")

        // 2. Kembalikan session ke admin sebelum lanjut
        supabase.auth.signInWith(Email) {
            this.email    = adminEmail
            this.password = adminPassword
        }
        Log.d(TAG, "Session dikembalikan ke admin: $adminEmail")

        // 3. Update full_name kasir baru (trigger handle_new_user mengisi
        db.from("profiles")
            .update({ set("full_name", fullName) }) {
                filter { eq("id", uid) }
            }

        Log.d(TAG, "full_name kasir baru diperbarui: \"$fullName\"")
    }

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
                "Update gagal. Pastikan policy UPDATE admin sudah dibuat di Supabase:\n" +
                        "create policy \"profile_update_admin_all\" on public.profiles for update\n" +
                        "to public\n" +
                        "using (get_user_role() = 'admin')\n" +
                        "with check (get_user_role() = 'admin');"
            )
        }
    }
}
