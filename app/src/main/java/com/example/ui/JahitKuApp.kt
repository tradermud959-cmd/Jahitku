package com.example.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.JahitKuRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.JahitanFormScreen
import com.example.ui.screens.JahitanListScreen
import com.example.ui.screens.PengaturanScreen
import com.example.ui.screens.PermakanFormScreen
import com.example.ui.screens.PermakanListScreen
import com.example.ui.screens.RiwayatListScreen
import com.example.ui.screens.RiwayatDetailScreen
import com.example.ui.screens.TransaksiFormScreen
import com.example.viewmodel.JahitKuViewModel
import com.example.viewmodel.JahitKuViewModelFactory

@Composable
fun JahitKuApp(
    repository: JahitKuRepository,
    isFirstRun: Boolean = false,
    onFirstRunComplete: () -> Unit = {},
    initialNamaToko: String = "",
    onNamaTokoChanged: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val viewModel: JahitKuViewModel = viewModel(factory = JahitKuViewModelFactory(repository, isFirstRun, onFirstRunComplete, initialNamaToko, onNamaTokoChanged))

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(navController = navController, viewModel = viewModel)
        }
        composable("jahitan_list") {
            JahitanListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "jahitan_form?id={id}",
            arguments = listOf(androidx.navigation.navArgument("id") { defaultValue = -1L; type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            JahitanFormScreen(navController = navController, viewModel = viewModel, editId = id)
        }
        composable("permakan_list") {
            PermakanListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "permakan_form?id={id}",
            arguments = listOf(androidx.navigation.navArgument("id") { defaultValue = -1L; type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            PermakanFormScreen(navController = navController, viewModel = viewModel, editId = id)
        }
        composable("transaksi_form") {
            TransaksiFormScreen(navController = navController, viewModel = viewModel)
        }
        composable("riwayat_list") {
            RiwayatListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "riwayat_detail?id={id}",
            arguments = listOf(androidx.navigation.navArgument("id") { defaultValue = -1L; type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            RiwayatDetailScreen(navController = navController, viewModel = viewModel, transaksiId = id)
        }
        composable("pengaturan") {
            PengaturanScreen(navController = navController, viewModel = viewModel)
        }
    }
}
