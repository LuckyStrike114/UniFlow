package com.example.uniflow

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uniflow.ui.theme.UniFlowTheme


class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // dohvaćamo spremljenu vrijednost iz SharedPreferences
        val prefs = getSharedPreferences("uniflow_prefs", Context.MODE_PRIVATE)
        val initialDarkMode = prefs.getBoolean("dark_mode", false)

        setContent {
            var isDarkTheme by remember { mutableStateOf(initialDarkMode) }

            // spremi u SharedPreferences kad se promijeni
            LaunchedEffect(isDarkTheme) {
                prefs.edit().putBoolean("dark_mode", isDarkTheme).apply()
            }

            UniFlowTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(isDarkTheme: Boolean, onToggleTheme: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Postavke", style = MaterialTheme.typography.headlineMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dark mode")
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onToggleTheme
            )
        }
    }
}
