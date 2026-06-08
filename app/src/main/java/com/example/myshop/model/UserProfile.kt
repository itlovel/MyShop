package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Mapping ke tabel `profiles` di Supabase
 */
object Role {
    const val ADMIN   = "admin"
    const val CASHIER = "cashier"
}

@Serializable
data class UserProfile(
    @SerialName("id")         val id: String = "",
    @SerialName("full_name")  val fullName: String = "",
    @SerialName("role")       val role: String = Role.CASHIER,
    @SerialName("is_active")  val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
) {
    val isAdmin: Boolean get() = role == Role.ADMIN
    // Label yang ditampilkan di UI — bebas pakai bahasa Indonesia
    val roleLabel: String get() = when (role) {
        Role.ADMIN   -> "Admin"
        Role.CASHIER -> "Kasir"
        else         -> role
    }
}

/**
 * UserProfile yang sudah dilengkapi email dari Auth session
 */
data class UserProfileWithEmail(
    val profile: UserProfile,
    val email  : String = "",
) {
    val id        : String  get() = profile.id
    val fullName  : String  get() = profile.fullName
    val role      : String  get() = profile.role
    val isActive  : Boolean get() = profile.isActive
    val isAdmin   : Boolean get() = profile.isAdmin
    val roleLabel : String  get() = profile.roleLabel
}

/** Payload insert profil kasir baru */
@Serializable
data class UserProfileInsert(
    @SerialName("id")        val id: String,
    @SerialName("full_name") val fullName: String,
    // Gunakan Role.CASHIER agar nilai yang disimpan ke DB konsisten
    @SerialName("role")      val role: String,
    @SerialName("is_active") val isActive: Boolean,
)
