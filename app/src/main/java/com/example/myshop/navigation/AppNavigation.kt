package com.example.myshop.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.myshop.viewmodel.*

private val bottomNavRoutes = setOf(
    Screen.Beranda.route,
    Screen.Kasir.route,
    Screen.Kas.route,
    Screen.Stok.route,
    Screen.Biaya.route,
)

// Route yang mendapat TopBar dari Scaffold (tab utama + ProfileScreen)
private val topBarRoutes = bottomNavRoutes + setOf(Screen.Profile.route)

private fun routeToTitle(route: String?): String = when (route) {
    Screen.Beranda.route      -> "Beranda"
    Screen.Kasir.route        -> "Kasir - Transaksi Penjualan"
    Screen.Kas.route          -> "Manajemen Kas"
    Screen.Stok.route         -> "Stok Produk"
    Screen.TambahProduk.route -> "Tambah Produk"
    Screen.DetailProduk.route -> "Detail Produk"
    Screen.EditProduk.route   -> "Edit Produk"
    Screen.Biaya.route        -> "Biaya Operasional"
    Screen.Profile.route      -> "Profil"
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

    val kasViewModel    : KasViewModel     = viewModel()
    val kasirViewModel  : KasirViewModel   = viewModel()
    val produkViewModel : ProdukViewModel  = viewModel()

    // ProfileViewModel di-hoist di sini agar tidak di-recreate setiap buka ProfileScreen
    val profileViewModel: ProfileViewModel = viewModel()

    // Setiap kali MainNavHost dibuat ulang dengan startDestination = Beranda
    // (terjadi setelah login berhasil), muat ulang profil agar tidak pakai data user lama.
    LaunchedEffect(startDestination) {
        if (startDestination == Screen.Beranda.route) {
            profileViewModel.muatProfilSaya()
        }
    }

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            // Simpan kredensial admin untuk re-login setelah signUpWith kasir baru
            profileViewModel.simpanKredensialAdmin(
                email    = email.value,
                password = password.value,
            )
            navController.navigate(Screen.Beranda.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val showTopBar      = currentRoute in topBarRoutes
    val showBottomBar   = currentRoute in bottomNavRoutes
    val isProfileScreen = currentRoute == Screen.Profile.route

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopBar(
                    title           = routeToTitle(currentRoute),
                    showBackButton  = isProfileScreen,
                    onBackClick     = { navController.popBackStack() },
                    showProfileIcon = !isProfileScreen,
                    onProfileClick  = { navController.navigate(Screen.Profile.route) },
                )
            }
        },
        bottomBar = {
            if (showBottomBar) BottomNavBar(navController = navController)
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

            // Tab utama
            composable(Screen.Beranda.route) {
                BerandaScreen(
                    onLogoutClick = {
                        profileViewModel.bersihkanKredensial()
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Kasir.route) {
                val vm: KasirViewModel = viewModel()
                KasirScreen(vm = vm)
            }

            composable(Screen.Kas.route) {
                val daftarKas by kasViewModel.daftarKas.collectAsStateWithLifecycle()
                KasListScreen(
                    daftarKas        = daftarKas,
                    onTambahKasClick = { navController.navigate(Screen.TambahKas.route) },
                    onDetailClick    = { kas ->
                        kasViewModel.muatDetailKas(kas)
                        navController.navigate(Screen.DetailKas.route)
                    },
                    onToggleStatus = kasViewModel::toggleStatusKas
                )
            }

            composable(Screen.Stok.route) {
                StokScreen(
                    vm            = produkViewModel,
                    onTambahClick = { navController.navigate(Screen.TambahProduk.route) },
                    onDetailClick = { produkId ->
                        navController.navigate(Screen.DetailProduk.createRoute(produkId))
                    }
                )
            }

            composable(Screen.Biaya.route) { BiayaScreen() }

            // Sub-screen Kas
            composable(Screen.TambahKas.route) {
                val kasUiState by kasViewModel.uiState.collectAsStateWithLifecycle()
                val namaKas    by kasViewModel.namaKas.collectAsStateWithLifecycle()
                val saldoAwal  by kasViewModel.saldoAwal.collectAsStateWithLifecycle()

                LaunchedEffect(kasUiState) {
                    if (kasUiState is KasUiState.Success) {
                        navController.popBackStack()
                        kasViewModel.resetState()
                    }
                }

                TambahKasScreen(
                    namaKas           = namaKas,
                    saldoAwal         = saldoAwal,
                    uiState           = kasUiState,
                    onNamaKasChange   = kasViewModel::onNamaKasChange,
                    onSaldoAwalChange = kasViewModel::onSaldoAwalChange,
                    onSimpanClick     = kasViewModel::tambahKas,
                    onNavigateBack    = { navController.popBackStack() }
                )
            }

            composable(Screen.DetailKas.route) {
                val kasTerpilih by kasViewModel.kasTerpilih.collectAsStateWithLifecycle()
                val logKas      by kasViewModel.logKas.collectAsStateWithLifecycle()
                val kasUiState  by kasViewModel.uiState.collectAsStateWithLifecycle()

                KasLogScreen(
                    kas                      = kasTerpilih,
                    logs                     = logKas,
                    uiState                  = kasUiState,
                    onNavigateBack           = { navController.popBackStack() },
                    onManualTransactionClick = {}
                )
            }

            // Sub-screen Produk
            composable(Screen.TambahProduk.route) {
                TambahProdukScreen(
                    onBackClick = {
                        produkViewModel.getProduk()
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route     = Screen.DetailProduk.route,
                arguments = listOf(navArgument("produkId") { type = NavType.StringType })
            ) { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId").orEmpty()
                DetailProdukScreen(
                    produkId    = produkId,
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
                route     = Screen.EditProduk.route,
                arguments = listOf(navArgument("produkId") { type = NavType.StringType })
            ) { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId").orEmpty()
                EditProdukScreen(
                    produkId    = produkId,
                    onBackClick = {
                        produkViewModel.getProduk()
                        navController.popBackStack()
                    }
                )
            }

            // Profil
            composable(Screen.Profile.route) {
                ProfileScreen(
                    vm             = profileViewModel,
                    onLogout       = {
                        profileViewModel.bersihkanKredensial()
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
