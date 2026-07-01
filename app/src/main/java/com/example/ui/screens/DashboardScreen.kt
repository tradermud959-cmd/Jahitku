package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.viewmodel.JahitKuViewModel
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(navController: NavController, viewModel: JahitKuViewModel) {
    val transaksis by viewModel.allTransaksi.collectAsStateWithLifecycle()

    val pendapatanJahitan = transaksis.sumOf { t ->
        viewModel.parseTransaksiItems(t.itemsJson).filter { it.jenis == "Jahitan" }.sumOf { it.harga }
    }
    val pendapatanPermakan = transaksis.sumOf { t ->
        viewModel.parseTransaksiItems(t.itemsJson).filter { it.jenis == "Permakan" }.sumOf { it.harga }
    }

    val formatRupiah = { amount: Double ->
        "Rp${NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)}"
    }

    Scaffold(
        topBar = { DashboardHeader(viewModel) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "RINGKASAN PENDAPATAN",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Jahitan",
                    amount = formatRupiah(pendapatanJahitan),
                    icon = Icons.Outlined.Checkroom,
                    amountColor = AccentCyan
                )
                SummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Permakan",
                    amount = formatRupiah(pendapatanPermakan),
                    icon = Icons.Outlined.ContentCut,
                    amountColor = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MENU UTAMA",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MenuCard(
                        title = "Jahitan",
                        emoji = "🧵",
                        onClick = { navController.navigate("jahitan_list") }
                    )
                }
                item {
                    MenuCard(
                        title = "Permakan",
                        emoji = "✂️",
                        onClick = { navController.navigate("permakan_list") }
                    )
                }
                item {
                    SpecialMenuCard(
                        title = "Transaksi",
                        emoji = "💳",
                        onClick = { navController.navigate("transaksi_form") }
                    )
                }
                item {
                    MenuCard(
                        title = "Riwayat",
                        emoji = "📜",
                        onClick = { navController.navigate("riwayat_list") }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    FullWidthMenuCard(
                        title = "Pengaturan Aplikasi",
                        emoji = "⚙️",
                        onClick = { navController.navigate("pengaturan") }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(viewModel: JahitKuViewModel) {
    val namaToko by viewModel.namaToko.collectAsStateWithLifecycle()
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("Jahit")
                    }
                    withStyle(style = SpanStyle(color = AccentCyan)) {
                        append("Ku")
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(AccentCyan.copy(alpha = alpha), CircleShape)
                )
                Text(
                    text = namaToko.ifEmpty { "Silakan isi nama toko di Pengaturan" },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(CardDark, CircleShape)
                .border(1.dp, Color(0xFF334155), CircleShape)
                .clickable { /* Profile / Notification action */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = AccentCyan,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SummaryCard(modifier: Modifier = Modifier, title: String, amount: String, icon: ImageVector, amountColor: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = TextSecondary, fontSize = 10.sp)
                Text(amount, color = amountColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MenuCard(title: String, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 28.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
        }
    }
}

@Composable
fun SpecialMenuCard(title: String, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AccentCyan.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 28.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AccentCyan)
        }
    }
}

@Composable
fun FullWidthMenuCard(title: String, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF334155).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
        }
    }
}

