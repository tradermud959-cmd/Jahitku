package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.JahitKuDatabase
import com.example.data.JahitKuRepository
import com.example.ui.JahitKuApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = JahitKuDatabase.getDatabase(this)
        val repository = JahitKuRepository(database)
        
        val prefs = getSharedPreferences("jahitku_prefs", MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("is_first_run", true)
        val initialNamaToko = prefs.getString("nama_toko", "") ?: ""

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    JahitKuApp(
                        repository = repository,
                        isFirstRun = isFirstRun,
                        onFirstRunComplete = {
                            prefs.edit().putBoolean("is_first_run", false).apply()
                        },
                        initialNamaToko = initialNamaToko,
                        onNamaTokoChanged = { newName ->
                            prefs.edit().putString("nama_toko", newName).apply()
                        }
                    )
                }
            }
        }
    }
}
