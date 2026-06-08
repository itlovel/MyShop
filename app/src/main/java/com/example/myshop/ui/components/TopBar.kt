package com.example.myshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.ui.theme.CardWhite
import com.example.myshop.ui.theme.NavyPrimary

/**
 * TopBar:
 * - Kiri   : hamburger menu (di tab utama) atau back arrow (di ProfileScreen)
 * - Tengah : judul halaman
 * - Kanan  : ikon profil
 */
@Composable
fun TopBar(
    title          : String,
    showBackButton : Boolean  = false,
    onBackClick    : () -> Unit = {},
    onMenuClick    : () -> Unit = {},
    onProfileClick : () -> Unit = {},   // kosong = ikon ditampilkan tapi tidak aktif (ProfileScreen)
    showProfileIcon: Boolean   = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyPrimary)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Kiri: back button (ProfileScreen) atau hamburger (tab utama)
        if (showBackButton) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint               = CardWhite
                )
            }
        } else {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector        = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint               = CardWhite
                )
            }
        }

        // Tengah: judul
        Text(
            text       = title,
            color      = CardWhite,
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Kanan: ikon profil
        if (showProfileIcon) {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector        = Icons.Default.AccountCircle,
                    contentDescription = "Profil",
                    tint               = CardWhite
                )
            }
        } else {
            // Spacer agar judul tetap di tengah
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

/**
 * DetailTopBar — dipakai di screen detail yang mengelola TopBar sendiri
 */
@Composable
fun DetailTopBar(
    title      : String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(NavyPrimary)
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint               = CardWhite
            )
        }

        Text(
            text       = title,
            color      = CardWhite,
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(48.dp))
    }
}
