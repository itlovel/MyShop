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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myshop.ui.components.BottomNavBar
import com.example.myshop.ui.components.TopBar
import com.example.myshop.ui.screen.*
import com.example.myshop.ui.theme.NavyPrimary
import com.example.myshop.viewmodel.AuthCheckState
import com.example.myshop.viewmodel.AuthUiState
import com.example.myshop.viewmodel.AuthViewModel
import com.example.myshop.viewmodel.KasirViewModel
import com.example.myshop.viewmodel.ProdukViewModel

private val bottomNavRoutes = setOf(
    Screen.Beranda.route,
    Screen.Kasir.route,
    Screen.Kas.route,
    Screen.Stok.route,
    Screen.Biaya.route,
)

private val chromeRoutes = bottomNavRoutes + setOf(
    Screen.TambahProduk.route,
    Screen.DetailProduk.route,
    Screen.EditProduk.route,
)

private fun routeToTitle(route: String?): String = when (route) {
    Screen.Beranda.route      -> "Beranda"
    Screen.Kasir.route        -> "Kasir - Transaksi Penjualan"
    Screen.Kas.route          -> "Manajemen Kas"
    Screen.Stok.route         -> "Stok Produk"
    Screen.TambahProduk.route -> "Tambah Produk"
    Screen.DetailProduk.route -> "Detail Produk"
    Screen.EditProduk.route   -> "Edit Produk"
    Screen.Biaya.route        -> "Biaya Operasional"
    else                      -> "Toko-I"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyPrimary)
            }
        }
        is AuthCheckState.Authenticated    -> MainNavHost(authViewModel, Screen.Beranda.route)
        is AuthCheckState.NotAuthenticated -> MainNavHost(authViewModel, Screen.Login.route)
    }
}

@Composable
fun MainNavHost(authViewModel: AuthViewModel, startDestination: String) {
    val navController = rememberNavController()

    val email    = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState  = authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Beranda.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showChrome   = currentRoute in chromeRoutes

    // KasirViewModel di-hoist di sini agar tidak di-recreate setiap navigasi
    val kasirViewModel: KasirViewModel = viewModel()
    val produkViewModel: ProdukViewModel = viewModel()

    Scaffold(
        topBar = {
            if (showChrome) TopBar(title = routeToTitle(currentRoute))
        },
        bottomBar = {
            if (showChrome) BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    email                = email.value,
                    password             = password.value,
                    uiState              = uiState.value,
                    onEmailChange        = authViewModel::onEmailChange,
                    onPasswordChange     = authViewModel::onPasswordChange,
                    onLoginClick         = authViewModel::login,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
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

            composable(Screen.Kasir.route) {
                KasirScreen(vm = kasirViewModel)
            }

            composable(Screen.Kas.route)   { KasScreen() }

            composable(Screen.Stok.route) {
                StokScreen(
                    vm = produkViewModel,
                    onTambahClick = {
                        navController.navigate(Screen.TambahProduk.route)
                    },
                    onDetailClick = { produkId ->
                        navController.navigate(Screen.DetailProduk.createRoute(produkId))
                    }
                )
            }

            composable(Screen.TambahProduk.route) {
                TambahProdukScreen(
                    onBackClick = {
                        produkViewModel.getProduk()
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.DetailProduk.route,
                arguments = listOf(navArgument("produkId") { type = NavType.StringType })
            ) { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId").orEmpty()

                DetailProdukScreen(
                    produkId = produkId,
                    onBackClick = {
                        produkViewModel.getProduk()
                        navController.popBackStack()
                    },
                    onEditClick = {
                        navController.navigate(Screen.EditProduk.createRoute(produkId))
                    }
                )
            }

            composable(
                route = Screen.EditProduk.route,
                arguments = listOf(navArgument("produkId") { type = NavType.StringType })
            ) { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId").orEmpty()

                EditProdukScreen(
                    produkId = produkId,
                    onBackClick = {
                        produkViewModel.getProduk()
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Biaya.route) { BiayaScreen() }
        }
    }
}
