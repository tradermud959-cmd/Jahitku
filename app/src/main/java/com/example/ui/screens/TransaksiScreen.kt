package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.TransaksiItem
import com.example.ui.theme.CardDark
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.JahitKuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiFormScreen(navController: NavController, viewModel: JahitKuViewModel) {
    val jahitanList by viewModel.allJahitan.collectAsStateWithLifecycle()
    val permakanList by viewModel.allPermakan.collectAsStateWithLifecycle()

    var namaPelanggan by remember { mutableStateOf("") }
    var nomorHp by remember { mutableStateOf("") }
    
    val currentItems = remember { mutableStateListOf<TransaksiItem>() }
    
    var showAddItemSheet by remember { mutableStateOf(false) }

    var uangPelangganText by remember { mutableStateOf("") }

    val totalHarga = currentItems.sumOf { it.harga }
    val uangPelanggan = uangPelangganText.toDoubleOrNull() ?: 0.0
    val kembalian = uangPelanggan - totalHarga
    val isUangCukup = uangPelanggan >= totalHarga && totalHarga > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaksi Baru", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = namaPelanggan,
                onValueChange = { namaPelanggan = it },
                label = { Text("Nama Pelanggan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = nomorHp,
                onValueChange = { nomorHp = it },
                label = { Text("Nomor HP") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daftar Item", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                OutlinedButton(onClick = { showAddItemSheet = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Item")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.itemName, fontWeight = FontWeight.Medium)
                                Text(item.jenis, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("Rp${item.harga.toLong()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            TextButton(onClick = { currentItems.remove(item) }, colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)) {
                                Text("Hapus")
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uangPelangganText,
                    onValueChange = { uangPelangganText = it },
                    label = { Text("Uang Pelanggan") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = if (totalHarga > 0) "Rp${totalHarga.toLong()}" else "Rp0",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Total") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    enabled = false
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kembalian", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    val kembalianColor = if (isUangCukup || uangPelanggan == 0.0) SuccessGreen else ErrorRed
                    val kembalianDisplay = if (kembalian < 0) "-Rp${(-kembalian).toLong()}" else "Rp${kembalian.toLong()}"
                    
                    Text(
                        text = kembalianDisplay,
                        color = kembalianColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = {
                    if (namaPelanggan.isNotBlank() && currentItems.isNotEmpty() && isUangCukup) {
                        viewModel.addTransaksi(
                            namaPelanggan = namaPelanggan,
                            nomorHp = nomorHp,
                            items = currentItems.toList(),
                            uangPelanggan = uangPelanggan
                        )
                        navController.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = isUangCukup && currentItems.isNotEmpty() && namaPelanggan.isNotBlank()
            ) {
                Text("Simpan Transaksi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddItemSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddItemSheet = false },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            var jenisTransaksi by remember { mutableStateOf("Jahitan") }
            var selectedItemId by remember { mutableStateOf(-1L) }
            var selectedItemName by remember { mutableStateOf("") }
            var harga by remember { mutableDoubleStateOf(0.0) }
            var expanded by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Tambah Item Transaksi", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Jenis:", modifier = Modifier.weight(0.3f), fontWeight = FontWeight.Medium)
                    Row(modifier = Modifier.weight(0.7f), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = jenisTransaksi == "Jahitan",
                            onClick = { 
                                jenisTransaksi = "Jahitan" 
                                selectedItemId = -1L
                                selectedItemName = ""
                                harga = 0.0
                            }
                        )
                        Text("Jahitan")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = jenisTransaksi == "Permakan",
                            onClick = { 
                                jenisTransaksi = "Permakan" 
                                selectedItemId = -1L
                                selectedItemName = ""
                                harga = 0.0
                            }
                        )
                        Text("Permakan")
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedItemName.ifEmpty { "Pilih Item" },
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Pilih $jenisTransaksi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = CardDark
                    ) {
                        val items = if (jenisTransaksi == "Jahitan") jahitanList else permakanList
                        items.forEach { item ->
                            val id = if (jenisTransaksi == "Jahitan") (item as com.example.data.Jahitan).id else (item as com.example.data.Permakan).id
                            val name = if (jenisTransaksi == "Jahitan") (item as com.example.data.Jahitan).nama else (item as com.example.data.Permakan).nama
                            val itemHarga = if (jenisTransaksi == "Jahitan") (item as com.example.data.Jahitan).harga else (item as com.example.data.Permakan).harga

                            DropdownMenuItem(
                                text = { Text("$name - Rp${itemHarga.toLong()}") },
                                onClick = {
                                    selectedItemId = id
                                    selectedItemName = name
                                    harga = itemHarga
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = "Rp${harga.toLong()}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Harga") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = false
                )

                Button(
                    onClick = {
                        if (selectedItemId != -1L) {
                            currentItems.add(
                                TransaksiItem(
                                    jenis = jenisTransaksi,
                                    itemId = selectedItemId,
                                    itemName = selectedItemName,
                                    harga = harga
                                )
                            )
                            showAddItemSheet = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedItemId != -1L
                ) {
                    Text("Tambahkan ke Daftar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
