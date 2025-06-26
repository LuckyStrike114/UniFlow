package com.example.uniflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uniflow.ui.theme.UniFlowTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("uniflow_prefs", Context.MODE_PRIVATE)
        val initialDarkMode = prefs.getBoolean("dark_mode", false)
        val initialTextSize = prefs.getString("text_size", "Normal") ?: "Normal"
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: ""

        setContent {
            var isDarkTheme by remember { mutableStateOf(initialDarkMode) }
            var textSize by remember { mutableStateOf(initialTextSize) }

            LaunchedEffect(isDarkTheme, textSize) {
                prefs.edit()
                    .putBoolean("dark_mode", isDarkTheme)
                    .putString("text_size", textSize)
                    .apply()
            }

            UniFlowTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = it },
                        textSize = textSize,
                        onTextSizeChange = { textSize = it },
                        username = username
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    textSize: String,
    onTextSizeChange: (String) -> Unit,
    username: String
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Postavke", fontSize = 30.sp, modifier = Modifier.padding(bottom = 8.dp))

        // Dark mode
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tamna tema")
            Switch(checked = isDarkTheme, onCheckedChange = onToggleTheme)
        }

        // Veličina teksta
        Column {
            Text("Veličina teksta")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Mala", "Normalna", "Velika").forEach {
                    FilterChip(
                        selected = textSize == it,
                        onClick = { onTextSizeChange(it) },
                        label = { Text(it) }
                    )
                }
            }
        }

        Divider()

        // Reset obaveza
        Button(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
        ) {
            Text("Resetiraj sve obaveze", color = MaterialTheme.colorScheme.onError)
        }

        // Odjava
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Odjava", color = MaterialTheme.colorScheme.onError)
        }
    }

    // Potvrda odjave
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Potvrda odjave") },
            text = { Text("Jeste li sigurni da se želite odjaviti?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }) {
                    Text("Da")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Ne")
                }
            }
        )
    }

    // Potvrda brisanja obaveza
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Brisanje obaveza") },
            text = { Text("Želite li izbrisati sve obaveze korisnika $username? Ova radnja je nepovratna.") },
            confirmButton = {
                TextButton(onClick = {
                    val success = dbHelper.deleteAllTasksForUser(username)
                    showResetDialog = false
                    Toast.makeText(context,
                        if (success) "Sve obaveze su izbrisane." else "Nema obaveza za brisanje.",
                        Toast.LENGTH_SHORT).show()
                }) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Odustani")
                }
            }
        )
    }
}
