package com.example.myshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.ui.theme.CardWhite
import com.example.myshop.ui.theme.NavyPrimary
import com.example.myshop.ui.theme.NotifBadge

/**
 * TopBar:
 * - Kiri  : hamburger menu
 * - Tengah: judul halaman
 * - Kanan : ikon notifikasi dengan badge kuning
 */
@Composable
fun TopBar(
    title           : String,
    onMenuClick     : () -> Unit = {},
    onNotifClick    : () -> Unit = {},
    hasNotification : Boolean    = true
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
        // Hamburger menu
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector        = Icons.Default.Menu,
                contentDescription = "Menu",
                tint               = CardWhite
            )
        }

        // Judul
        Text(
            text       = title,
            color      = CardWhite,
            fontSize   = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Notifikasi + badge
        Box {
            IconButton(onClick = onNotifClick) {
                Icon(
                    imageVector        = Icons.Default.NotificationsNone,
                    contentDescription = "Notifikasi",
                    tint               = CardWhite
                )
            }
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NotifBadge, shape = androidx.compose.foundation.shape.CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                )
            }
        }
    }
}
