package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("id")         val id: String = "",
    @SerialName("full_name")  val fullName: String = "",
    @SerialName("role")       val role: String = "kasir",
    @SerialName("is_active")  val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
) {
    val isAdmin: Boolean get() = role == "admin"
    val roleLabel: String get() = if (isAdmin) "Admin" else "Kasir"
}

/**
 * UserProfile yang sudah dilengkapi email dari Auth session.
 * Dipakai di ViewModel dan UI dan bukan hasil decode langsung dari DB
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

/** Payload insert profil kasir baru (kolom yang dikirim ke DB) */
@Serializable
data class UserProfileInsert(
    @SerialName("id")        val id: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("role")      val role: String = "kasir",
    @SerialName("is_active") val isActive: Boolean = true,
)
