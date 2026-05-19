package com.example.myshop.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.model.Kas
import com.example.myshop.model.KeranjangItem
import com.example.myshop.model.Pelanggan
import com.example.myshop.model.Produk
import com.example.myshop.model.RingkasanTransaksi
import com.example.myshop.ui.theme.*
import com.example.myshop.viewmodel.KasirUiState
import com.example.myshop.viewmodel.KasirViewModel
import java.text.NumberFormat
import java.util.Locale

private fun formatRupiah(value: Double): String {
    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${fmt.format(value.toLong())}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasirScreen(vm: KasirViewModel = viewModel()) {
    val produkList         by vm.produkList.collectAsStateWithLifecycle()
    val pelangganList      by vm.pelangganList.collectAsStateWithLifecycle()
    val kasList            by vm.kasList.collectAsStateWithLifecycle()
    val pelangganDipilih   by vm.pelangganDipilih.collectAsStateWithLifecycle()
    val kasDipilih         by vm.kasDipilih.collectAsStateWithLifecycle()
    val keranjang          by vm.keranjang.collectAsStateWithLifecycle()
    val searchQuery        by vm.searchQuery.collectAsStateWithLifecycle()
    val jumlahBayar        by vm.jumlahBayar.collectAsStateWithLifecycle()
    val uiState            by vm.uiState.collectAsStateWithLifecycle()
    val isLoadingData      by vm.isLoadingData.collectAsStateWithLifecycle()
    val showPelangganSheet by vm.showPelangganSheet.collectAsStateWithLifecycle()
    val totalBelanja       by vm.totalBelanja.collectAsStateWithLifecycle()
    val kembalian          by vm.kembalian.collectAsStateWithLifecycle()

    val produkFiltered = remember(produkList, searchQuery) {
        if (searchQuery.isBlank()) produkList
        else produkList.filter { it.nama.contains(searchQuery, ignoreCase = true) }
    }

    // Dialog sukses,  ringkasan dibawa dari dalam state sehingga tidak pernah Rp 0
    if (uiState is KasirUiState.Success) {
        val ringkasan = (uiState as KasirUiState.Success).ringkasan
        TransaksiSuksesDialog(
            ringkasan = ringkasan,
            onDismiss = { vm.resetUiState() }
        )
    }

    if (uiState is KasirUiState.Error) {
        AlertDialog(
            onDismissRequest = { vm.resetUiState() },
            title   = { Text("Perhatian") },
            text    = { Text((uiState as KasirUiState.Error).message) },
            confirmButton = {
                TextButton(onClick = { vm.resetUiState() }) { Text("OK") }
            }
        )
    }

    if (showPelangganSheet) {
        PelangganBottomSheet(
            pelangganList = pelangganList,
            onPilih       = vm::pilihPelanggan,
            onDismiss     = vm::hidePilihPelanggan,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Pelanggan
        SectionCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "PELANGGAN",
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = TextSecondary,
                    letterSpacing = 0.8.sp,
                    modifier      = Modifier.weight(1f)
                )
                // Badge wajib
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ErrorRed.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("*", fontSize = 10.sp, color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(10.dp))

            if (pelangganDipilih == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(BorderStroke(1.dp, DashedBorder), RoundedCornerShape(8.dp))
                        .clickable { vm.showPilihPelanggan() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+ Pilih Pelanggan", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                PelangganTerpilihCard(pelanggan = pelangganDipilih!!, onHapus = vm::hapusPelanggan)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Katalog Produk
        SectionCard {
            Text(
                "KATALOG PRODUK",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = TextSecondary,
                letterSpacing = 0.8.sp
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value         = searchQuery,
                onValueChange = vm::onSearchChange,
                placeholder   = { Text("Cari produk...", color = TextSecondary) },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                shape         = RoundedCornerShape(10.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderColor,
                    focusedBorderColor   = NavyPrimary,
                )
            )

            Spacer(Modifier.height(8.dp))

            when {
                isLoadingData -> {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NavyPrimary, modifier = Modifier.size(28.dp))
                    }
                }
                produkFiltered.isEmpty() -> {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (searchQuery.isBlank()) "Belum ada produk" else "Produk tidak ditemukan",
                            color = TextSecondary
                        )
                        if (searchQuery.isBlank()) {
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = vm::retryLoad) {
                                Text("Muat ulang", color = NavyPrimary)
                            }
                        }
                    }
                }
                else -> {
                    produkFiltered.forEach { produk ->
                        ProdukItem(produk = produk, onTambah = { vm.tambahKeKeranjang(produk) })
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Keranjang Belanja
        SectionCard {
            Text(
                "KERANJANG BELANJA",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = TextSecondary,
                letterSpacing = 0.8.sp
            )
            Spacer(Modifier.height(12.dp))

            if (keranjang.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keranjang kosong", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                keranjang.forEach { item ->
                    KeranjangItemRow(
                        item     = item,
                        onTambah = { vm.tambahKeKeranjang(item.produk) },
                        onKurang = { vm.kurangiDariKeranjang(item.produk.id) },
                        onHapus  = { vm.hapusDariKeranjang(item.produk.id) },
                    )
                    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                }

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        formatRupiah(totalBelanja),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = NavyPrimary
                    )
                }
            }
        }

        if (keranjang.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))

            // Pembayaran
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavyPrimary)
                    .border(BorderStroke(2.dp, YellowAccent), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "PEMBAYARAN",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = CardWhite,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(12.dp))

                    Text("Akun Kas Tujuan *", fontSize = 12.sp, color = CardWhite)
                    Spacer(Modifier.height(6.dp))

                    var kasDropdownExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded         = kasDropdownExpanded,
                        onExpandedChange = { kasDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value         = kasDipilih?.namaKas ?: "Pilih Akun Kas",
                            onValueChange = {},
                            readOnly      = true,
                            trailingIcon  = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = kasDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape  = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor    = CardWhite,
                                focusedBorderColor      = YellowAccent,
                                unfocusedContainerColor = CardWhite,
                                focusedContainerColor   = CardWhite,
                                unfocusedTextColor      = if (kasDipilih == null) TextSecondary else TextPrimary,
                            )
                        )
                        ExposedDropdownMenu(
                            expanded         = kasDropdownExpanded,
                            onDismissRequest = { kasDropdownExpanded = false }
                        ) {
                            kasList.forEach { kas ->
                                DropdownMenuItem(
                                    text    = { Text(kas.namaKas) },
                                    onClick = {
                                        vm.pilihKas(kas)
                                        kasDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("UANG DITERIMA", fontSize = 11.sp, color = CardWhite, letterSpacing = 0.8.sp)
                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value           = jumlahBayar,
                        onValueChange   = vm::onJumlahBayarChange,
                        placeholder     = {
                            Text("0", color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        },
                        modifier        = Modifier.fillMaxWidth(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle       = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 18.sp),
                        shape           = RoundedCornerShape(10.dp),
                        colors          = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor    = CardWhite,
                            focusedBorderColor      = YellowAccent,
                            unfocusedContainerColor = CardWhite,
                            focusedContainerColor   = CardWhite,
                        )
                    )

                    val bayarNum = jumlahBayar.toDoubleOrNull() ?: 0.0
                    if (bayarNum >= totalBelanja && jumlahBayar.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardWhite.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kembalian", color = CardWhite, fontSize = 13.sp)
                            Text(formatRupiah(kembalian), color = YellowAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    val bisaSelesai = pelangganDipilih != null &&
                            kasDipilih != null &&
                            jumlahBayar.isNotBlank() &&
                            bayarNum >= totalBelanja

                    Button(
                        onClick  = vm::selesaikanTransaksi,
                        enabled  = bisaSelesai && uiState !is KasirUiState.Loading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = YellowAccent,
                            disabledContainerColor = YellowAccent.copy(alpha = 0.4f)
                        )
                    ) {
                        if (uiState is KasirUiState.Loading) {
                            CircularProgressIndicator(color = NavyPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                        } else {
                            Text("SELESAIKAN TRANSAKSI", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// Sub-composables

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun PelangganTerpilihCard(pelanggan: Pelanggan, onHapus: () -> Unit) {
    Row(
        modifier          = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(NavyPrimary).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(44.dp).clip(CircleShape).background(YellowAccent),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(pelanggan.nama, color = CardWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            if (!pelanggan.noTelp.isNullOrBlank()) {
                Text(pelanggan.noTelp, color = CardWhite.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }
        IconButton(onClick = onHapus) {
            Icon(Icons.Default.Delete, null, tint = ErrorRed)
        }
    }
}

@Composable
private fun ProdukItem(produk: Produk, onTambah: () -> Unit) {
    Row(
        modifier          = Modifier.fillMaxWidth().clickable(onClick = onTambah).padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(ProductIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ShoppingBasket, null, tint = ProductIconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(produk.nama, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(formatRupiah(produk.hargaJual), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
            Text("Stok: ${produk.stok.toInt()}", fontSize = 11.sp, color = StockTextColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun KeranjangItemRow(
    item    : KeranjangItem,
    onTambah: () -> Unit,
    onKurang: () -> Unit,
    onHapus : () -> Unit,
) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.produk.nama, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${formatRupiah(item.produk.hargaJual)} × ${item.quantity}", fontSize = 11.sp, color = TextSecondary)
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier         = Modifier.size(30.dp).clip(CircleShape).background(ErrorRed.copy(alpha = 0.12f)).clickable(onClick = onKurang),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Remove, null, tint = ErrorRed, modifier = Modifier.size(16.dp)) }
        Spacer(Modifier.width(8.dp))
        Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier         = Modifier.size(30.dp).clip(RoundedCornerShape(6.dp)).background(ProductIconTint).clickable(onClick = onTambah),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Add, null, tint = CardWhite, modifier = Modifier.size(16.dp)) }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onHapus, modifier = Modifier.size(30.dp)) {
            Icon(Icons.Default.Delete, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(4.dp))
        Text(formatRupiah(item.subTotal), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = NavyPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PelangganBottomSheet(
    pelangganList : List<Pelanggan>,
    onPilih       : (Pelanggan) -> Unit,
    onDismiss     : () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(40.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(BorderColor)
            )
        }
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Pilih Pelanggan", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
            Spacer(Modifier.height(16.dp))

            if (pelangganList.isEmpty()) {
                Text("Belum ada pelanggan", color = TextSecondary, modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                pelangganList.forEach { p ->
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundLight)
                            .clickable { onPilih(p) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier         = Modifier.size(44.dp).clip(CircleShape).background(NavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.nama.firstOrNull()?.uppercase() ?: "?", color = CardWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(p.nama, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
                            if (!p.noTelp.isNullOrBlank()) {
                                Text(p.noTelp, fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TransaksiSuksesDialog(
    ringkasan: RingkasanTransaksi,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardWhite,
        icon = {
            Box(
                modifier         = Modifier.size(56.dp).clip(CircleShape).background(SuccessGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
            }
        },
        title = {
            Text("Transaksi Berhasil!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                InfoBaris("Total",     formatRupiah(ringkasan.total))
                InfoBaris("Dibayar",   formatRupiah(ringkasan.bayar))
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                InfoBaris("Kembalian", formatRupiah(ringkasan.kembalian), warnaNilai = SuccessGreen)
            }
        },
        confirmButton = {
            Button(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) { Text("Tutup") }
        }
    )
}

@Composable
private fun InfoBaris(label: String, nilai: String, warnaNilai: Color = TextPrimary) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(nilai, color = warnaNilai, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
