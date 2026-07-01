package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.Permakan
import com.example.ui.theme.CardDark
import com.example.viewmodel.JahitKuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermakanListScreen(navController: NavController, viewModel: JahitKuViewModel) {
    val allPermakan by viewModel.allPermakan.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var deleteItem by remember { mutableStateOf<Permakan?>(null) }
    
    val filteredList = allPermakan.filter { 
        it.nama.contains(searchQuery, ignoreCase = true) || it.kategori.contains(searchQuery, ignoreCase = true)
    }

    if (deleteItem != null) {
        AlertDialog(
            onDismissRequest = { deleteItem = null },
            title = { Text("Hapus Permakan") },
            text = { Text("Yakin ingin menghapus data ini?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePermakan(deleteItem!!)
                    deleteItem = null
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteItem = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permakan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("permakan_form") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Tambah Permakan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari permakan...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardDark,
                    focusedContainerColor = CardDark,
                    unfocusedBorderColor = CardDark,
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { permakan ->
                    PermakanCard(
                        permakan = permakan,
                        onEdit = { navController.navigate("permakan_form?id=${permakan.id}") },
                        onDelete = { deleteItem = permakan }
                    )
                }
            }
        }
    }
}

@Composable
fun PermakanCard(permakan: Permakan, onEdit: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(permakan.nama, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("Rp${permakan.harga.toLong()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(permakan.kategori, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Catatan:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Text(permakan.catatan.ifEmpty { "-" }, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onEdit) {
                        Text("✏ Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("🗑 Hapus")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermakanFormScreen(navController: NavController, viewModel: JahitKuViewModel, editId: Long = -1L) {
    val allPermakan by viewModel.allPermakan.collectAsStateWithLifecycle()
    val isEditing = editId != -1L
    val targetItem = allPermakan.find { it.id == editId }

    var nama by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }

    LaunchedEffect(targetItem) {
        if (isEditing && targetItem != null) {
            nama = targetItem.nama
            kategori = targetItem.kategori
            harga = targetItem.harga.toLong().toString()
            catatan = targetItem.catatan
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Permakan" else "Tambah Permakan", fontWeight = FontWeight.SemiBold) },
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
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Permakan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = kategori,
                onValueChange = { kategori = it },
                label = { Text("Kategori") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = harga,
                onValueChange = { harga = it },
                label = { Text("Harga Permakan") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (nama.isNotBlank() && harga.isNotBlank()) {
                        if (isEditing && targetItem != null) {
                            viewModel.updatePermakan(
                                targetItem.copy(
                                    nama = nama,
                                    kategori = kategori,
                                    harga = harga.toDoubleOrNull() ?: 0.0,
                                    catatan = catatan
                                )
                            )
                        } else {
                            viewModel.addPermakan(nama, kategori, harga.toDoubleOrNull() ?: 0.0, catatan)
                        }
                        navController.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
