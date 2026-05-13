package com.example.myshop.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myshop.navigation.bottomNavItems
import com.example.myshop.ui.theme.CardWhite
import com.example.myshop.ui.theme.NavyPrimary
import com.example.myshop.ui.theme.TextSecondary
import com.example.myshop.ui.theme.YellowAccent

/**
 * BottomNavigationBar:
 *  - Background navy gelap
 *  - Icon & label aktif: kuning (YellowAccent)
 *  - Icon & label tidak aktif: putih transparan
 */
@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = NavyPrimary,
        tonalElevation = 0.dp,
        modifier        = Modifier.height(64.dp)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = selected,
                onClick  = {
                    if (!selected) {
                        navController.navigate(item.screen.route) {
                            // Hindari back-stack menumpuk
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        modifier           = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text     = item.label,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = YellowAccent,
                    selectedTextColor   = YellowAccent,
                    unselectedIconColor = CardWhite.copy(alpha = 0.7f),
                    unselectedTextColor = CardWhite.copy(alpha = 0.7f),
                    indicatorColor      = NavyPrimary   // tanpa bulatan highlight
                ),
                alwaysShowLabel = true
            )
        }
    }
}