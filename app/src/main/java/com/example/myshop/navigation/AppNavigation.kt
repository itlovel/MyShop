package com.example.myshop.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myshop.ui.components.BottomNavBar
import com.example.myshop.ui.components.TopBar
import com.example.myshop.ui.screen.BerandaScreen
import com.example.myshop.ui.screen.BiayaScreen
import com.example.myshop.ui.screen.KasScreen
import com.example.myshop.ui.screen.KasirScreen
import com.example.myshop.ui.screen.LoginScreen
import com.example.myshop.ui.screen.RegisterScreen
import com.example.myshop.ui.screen.StokScreen
import com.example.myshop.ui.theme.NavyPrimary
import com.example.myshop.viewmodel.AuthCheckState
import com.example.myshop.viewmodel.AuthUiState
import com.example.myshop.viewmodel.AuthViewModel

private val bottomNavRoutes = setOf(
    Screen.Beranda.route,
    Screen.Kasir.route,
    Screen.Kas.route,
    Screen.Stok.route,
    Screen.Biaya.route,
)


//  Mapping route
private fun routeToTitle(route: String?): String = when (route) {
    Screen.Beranda.route -> "Beranda"
    Screen.Kasir.route   -> "Kasir - Transaksi Penjualan"
    Screen.Kas.route     -> "Manajemen Kas"
    Screen.Stok.route    -> "Stok Produk"
    Screen.Biaya.route   -> "Biaya Operasional"
    else                 -> "Toko-I"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = NavyPrimary) }
        }

        is AuthCheckState.Authenticated -> {
            MainNavHost(authViewModel = authViewModel, startDestination = Screen.Beranda.route)
        }

        is AuthCheckState.NotAuthenticated -> {
            MainNavHost(authViewModel = authViewModel, startDestination = Screen.Login.route)
        }
    }
}

//  Main NavHost dengan Scaffold (TopBar + BottomNav)
@Composable
fun MainNavHost(
    authViewModel    : AuthViewModel,
    startDestination : String
) {
    val navController = rememberNavController()

    val email    = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState  = authViewModel.uiState.collectAsStateWithLifecycle()

    // Navigasi setelah login/register berhasil → Beranda
    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Beranda.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    val currentRoute = navController
        .currentBackStackEntryAsState().value
        ?.destination?.route

    val showChrome = currentRoute in bottomNavRoutes

    Scaffold(
        topBar = {
            if (showChrome) {
                TopBar(title = routeToTitle(currentRoute))
            }
        },
        bottomBar = {
            if (showChrome) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    email               = email.value,
                    password            = password.value,
                    uiState             = uiState.value,
                    onEmailChange       = authViewModel::onEmailChange,
                    onPasswordChange    = authViewModel::onPasswordChange,
                    onLoginClick        = authViewModel::login,
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    email             = email.value,
                    password          = password.value,
                    uiState           = uiState.value,
                    onEmailChange     = authViewModel::onEmailChange,
                    onPasswordChange  = authViewModel::onPasswordChange,
                    onRegisterClick   = authViewModel::register,
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // Main (bottom nav)
            composable(Screen.Beranda.route) {
                BerandaScreen(
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Kasir.route) { KasirScreen() }
            composable(Screen.Kas.route)   { KasScreen() }
            composable(Screen.Stok.route)  { StokScreen() }
            composable(Screen.Biaya.route) { BiayaScreen() }
        }
    }
}