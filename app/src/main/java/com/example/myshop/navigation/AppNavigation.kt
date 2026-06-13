package com.example.myshop.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myshop.viewmodel.PelangganViewModel
import com.example.myshop.ui.components.BottomNavBar
import com.example.myshop.ui.components.TopBar
import com.example.myshop.ui.screen.*
import com.example.myshop.ui.theme.CardWhite
import com.example.myshop.ui.theme.NavyPrimary
import com.example.myshop.ui.theme.YellowAccent
import com.example.myshop.viewmodel.*
import kotlinx.coroutines.launch

private val bottomNavRoutes = setOf(
    Screen.Beranda.route,
    Screen.Kasir.route,
    Screen.Kas.route,
    Screen.Stok.route,
    Screen.Pengeluaran.route,
)

// Route yang mendapat TopBar dari Scaffold (tab utama + sub-screens)
private val topBarRoutes = bottomNavRoutes + setOf(
    Screen.Profile.route,
    Screen.Pelanggan.route,
    Screen.TambahPelanggan.route,
    Screen.EditPelanggan.route,
    Screen.PelangganLog.route,
    Screen.TambahKas.route,
    Screen.DetailKas.route
)

private val backButtonRoutes = setOf(
    Screen.Profile.route,
    Screen.Pelanggan.route,
    Screen.TambahPelanggan.route,
    Screen.EditPelanggan.route,
    Screen.PelangganLog.route,
    Screen.TambahKas.route,
    Screen.DetailKas.route
)

private fun routeToTitle(route: String?): String = when (route) {
    Screen.Beranda.route      -> "Beranda"
    Screen.Kasir.route        -> "Kasir - Transaksi Penjualan"
    Screen.Kas.route          -> "Manajemen Kas"
    Screen.Stok.route         -> "Stok Produk"
    Screen.TambahProduk.route -> "Tambah Produk"
    Screen.DetailProduk.route -> "Detail Produk"
    Screen.EditProduk.route   -> "Edit Produk"
    Screen.Pengeluaran.route  -> "Pengeluaran"
    Screen.Profile.route      -> "Profil"

    Screen.Pelanggan.route       -> "Pelanggan"
    Screen.TambahPelanggan.route -> "Tambah Pelanggan"
    Screen.EditPelanggan.route   -> "Edit Pelanggan"
    Screen.PelangganLog.route    -> "Riwayat Pelanggan"
    Screen.TambahKas.route       -> "Tambah Kas"
    Screen.DetailKas.route       -> "Detail Transaksi Kas"

    else                         -> "Toko-I"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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

    val email = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()

    val kasViewModel: KasViewModel = viewModel()
    val kasirViewModel: KasirViewModel = viewModel()
    val produkViewModel: ProdukViewModel = viewModel()
    val pengeluaranViewModel: PengeluaranViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val pelangganViewModel: PelangganViewModel = viewModel()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(startDestination) {
        if (startDestination == Screen.Beranda.route) {
            profileViewModel.muatProfilSaya()
        }
    }

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            profileViewModel.simpanKredensialAdmin(
                email = email.value,
                password = password.value,
            )
            navController.navigate(Screen.Beranda.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute in topBarRoutes
    val showBottomBar = currentRoute in bottomNavRoutes
    val showBackButton = currentRoute in backButtonRoutes
    val showDrawer = currentRoute != Screen.Login.route && currentRoute != Screen.Register.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showDrawer,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = NavyPrimary
            ) {
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp),
                    color = CardWhite
                )

                NavigationDrawerItem(
                    label = { Text("Pelanggan") },
                    selected = currentRoute == Screen.Pelanggan.route,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = YellowAccent,
                        unselectedContainerColor = Color.Transparent,
                        selectedTextColor = NavyPrimary,
                        unselectedTextColor = CardWhite,
                        selectedIconColor = NavyPrimary,
                        unselectedIconColor = CardWhite
                    ),
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Pelanggan.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    TopBar(
                        title = routeToTitle(currentRoute),
                        showBackButton = showBackButton,
                        onBackClick = { navController.popBackStack() },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        showProfileIcon = currentRoute != Screen.Profile.route,
                        onProfileClick = { navController.navigate(Screen.Profile.route) },
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) BottomNavBar(navController = navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                // --- AUTHENTICATION ---
                composable(Screen.Login.route) {
                    LoginScreen(
                        email = email.value,
                        password = password.value,
                        uiState = uiState.value,
                        onEmailChange = authViewModel::onEmailChange,
                        onPasswordChange = authViewModel::onPasswordChange,
                        onLoginClick = authViewModel::login,
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        email = email.value,
                        password = password.value,
                        uiState = uiState.value,
                        onEmailChange = authViewModel::onEmailChange,
                        onPasswordChange = authViewModel::onPasswordChange,
                        onRegisterClick = authViewModel::register,
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }

                // --- MAIN TABS ---
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
                    val kasUiState by kasViewModel.uiState.collectAsStateWithLifecycle()
                    val isLoadingData by kasViewModel.isLoadingData.collectAsStateWithLifecycle()

                    KasListScreen(
                        daftarKas = daftarKas,
                        uiState = kasUiState,
                        isLoadingData = isLoadingData,
                        onTambahKasClick = { navController.navigate(Screen.TambahKas.route) },
                        onDetailClick = { kas ->
                            kasViewModel.muatDetailKas(kas)
                            navController.navigate(Screen.DetailKas.route)
                        },
                        onToggleStatus = kasViewModel::toggleStatusKas,
                        onRetryLoad = kasViewModel::muatDataKas
                    )
                }

                composable(Screen.Stok.route) {
                    StokScreen(
                        vm = produkViewModel,
                        onTambahClick = { navController.navigate(Screen.TambahProduk.route) },
                        onDetailClick = { produkId ->
                            navController.navigate(Screen.DetailProduk.createRoute(produkId))
                        }
                    )
                }

                composable(Screen.Pengeluaran.route) {
                    PengeluaranScreen(
                        onTambahClick = { navController.navigate(Screen.TambahPengeluaran.route) },
                        onDetailClick = { pengeluaranId ->
                            navController.navigate(Screen.DetailPengeluaran.createRoute(pengeluaranId))
                        },
                        pengeluaranViewModel = pengeluaranViewModel
                    )
                }

                // --- SUB-SCREEN KAS ---
                composable(Screen.TambahKas.route) {
                    val kasUiState by kasViewModel.uiState.collectAsStateWithLifecycle()
                    val namaKas by kasViewModel.namaKas.collectAsStateWithLifecycle()
                    val saldoAwal by kasViewModel.saldoAwal.collectAsStateWithLifecycle()

                    LaunchedEffect(kasUiState) {
                        if (kasUiState is KasUiState.Success) {
                            navController.popBackStack()
                            kasViewModel.resetState()
                        }
                    }

                    TambahKasScreen(
                        namaKas = namaKas,
                        saldoAwal = saldoAwal,
                        uiState = kasUiState,
                        onNamaKasChange = kasViewModel::onNamaKasChange,
                        onSaldoAwalChange = kasViewModel::onSaldoAwalChange,
                        onSimpanClick = kasViewModel::tambahKas,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.DetailKas.route) {
                    val kasTerpilih by kasViewModel.kasTerpilih.collectAsStateWithLifecycle()
                    val logKas by kasViewModel.logKas.collectAsStateWithLifecycle()
                    val kasUiState by kasViewModel.uiState.collectAsStateWithLifecycle()

                    KasLogScreen(
                        kas = kasTerpilih,
                        logs = logKas,
                        uiState = kasUiState,
                        onNavigateBack = { navController.popBackStack() },
                        onManualTransactionClick = {}
                    )
                }

                // --- SUB-SCREEN PRODUK ---
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

                // --- SUB-SCREEN PENGELUARAN ---
                composable(Screen.TambahPengeluaran.route) {
                    TambahPengeluaranScreen(
                        onBackClick = {
                            pengeluaranViewModel.getPengeluaran()
                            kasViewModel.muatDataKas()
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.DetailPengeluaran.route,
                    arguments = listOf(navArgument("pengeluaranId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pengeluaranId = backStackEntry.arguments?.getString("pengeluaranId").orEmpty()
                    DetailPengeluaranScreen(
                        pengeluaranId = pengeluaranId,
                        onBackClick = { navController.popBackStack() },
                        pengeluaranViewModel = pengeluaranViewModel
                    )
                }

                // --- PELANGGAN MODULE ---
                composable(Screen.Pelanggan.route) {
                    PelangganListScreen(
                        viewModel = pelangganViewModel,
                        onAddClick = { navController.navigate(Screen.TambahPelanggan.route) },
                        onEditClick = { pelanggan ->
                            navController.navigate(Screen.EditPelanggan.createRoute(pelanggan.id))
                        },
                        onLogClick = { pelangganId ->
                            navController.navigate(Screen.PelangganLog.createRoute(pelangganId))
                        }
                    )
                }

                composable(Screen.TambahPelanggan.route) {
                    PelangganFormScreen(
                        pelanggan = null,
                        viewModel = pelangganViewModel,
                        onSaved = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.EditPelanggan.route,
                    arguments = listOf(navArgument("pelangganId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pelangganId = backStackEntry.arguments?.getString("pelangganId").orEmpty()
                    val selectedState by pelangganViewModel.selectedPelangganState.collectAsStateWithLifecycle()

                    LaunchedEffect(pelangganId) {
                        pelangganViewModel.resetFormState()
                        pelangganViewModel.fetchPelangganById(pelangganId)
                    }

                    when (val state = selectedState) {
                        UiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = NavyPrimary)
                            }
                        }
                        is UiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(state.message)
                            }
                        }
                        is UiState.Success -> {
                            state.data?.let { pelanggan ->
                                PelangganFormScreen(
                                    pelanggan = pelanggan,
                                    viewModel = pelangganViewModel,
                                    onSaved = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }

                composable(
                    route = Screen.PelangganLog.route,
                    arguments = listOf(navArgument("pelangganId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pelangganId = backStackEntry.arguments?.getString("pelangganId").orEmpty()
                    PelangganLogScreen(
                        pelangganId = pelangganId,
                        viewModel = pelangganViewModel
                    )
                }

                // --- PROFIL ---
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        vm = profileViewModel,
                        onLogout = {
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
}