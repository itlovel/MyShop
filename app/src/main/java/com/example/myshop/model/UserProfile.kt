package com.example.myshop.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("id")         val id: String = "",
    @SerialName("nama")       val nama: String = "",
    @SerialName("email")      val email: String = "",
    @SerialName("role")       val role: String = "kasir",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
) {
    val isAdmin: Boolean get() = role == "admin"
    val roleLabel: String get() = if (isAdmin) "Admin" else "Kasir"
}

/** Payload untuk insert profil kasir baru oleh admin */
@Serializable
data class UserProfileInsert(
    @SerialName("id")    val id: String,
    @SerialName("nama")  val nama: String,
    @SerialName("email") val email: String,
    @SerialName("role")  val role: String = "kasir",
)

/** Payload untuk update nama profil */
@Serializable
data class UserProfileUpdate(
    @SerialName("nama")       val nama: String,
    @SerialName("updated_at") val updatedAt: String,
)
