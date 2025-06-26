package com.example.uniflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uniflow.ui.theme.UniFlowTheme
import java.time.format.DateTimeFormatter

class MyTasksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: ""
        setContent {
            UniFlowTheme {
                TaskListScreen(username)
            }
        }
    }
}

@Composable
fun TaskListScreen(username: String) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val tasks = remember { mutableStateListOf<Task>() }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(true) {
        tasks.clear()
        tasks.addAll(dbHelper.getTasksForUser(username))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Moje obaveze", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(tasks) { task ->
                    val formattedDate = task.datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    val isLight = task.boja.luminance() > 0.5f
                    val textColor = if (isLight) Color.Black else Color.White

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(task.boja)
                            .clickable { editingTask = task }
                            .padding(12.dp)
                    ) {
                        Text("Datum: $formattedDate", color = textColor)
                        Text("Vrsta: ${task.vrsta}", color = textColor)
                        Text("Naziv: ${task.naziv}", color = textColor)
                        task.vrijeme?.let {
                            Text("Vrijeme: $it", color = textColor)
                        }
                    }
                }
            }
        }
    }

    editingTask?.let { task ->
        var newVrsta by remember { mutableStateOf(task.vrsta) }
        var newNaziv by remember { mutableStateOf(task.naziv) }
        var newVrijeme by remember { mutableStateOf(task.vrijeme ?: "") }

        AlertDialog(
            onDismissRequest = { editingTask = null },
            title = { Text("Uredi obavezu") },
            text = {
                Column {
                    OutlinedTextField(value = newVrsta, onValueChange = { newVrsta = it }, label = { Text("Vrsta") })
                    OutlinedTextField(value = newNaziv, onValueChange = { newNaziv = it }, label = { Text("Naziv") })
                    OutlinedTextField(value = newVrijeme, onValueChange = { newVrijeme = it }, label = { Text("Vrijeme") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    dbHelper.deleteTask(task)
                    val updatedTask = task.copy(vrsta = newVrsta, naziv = newNaziv, vrijeme = newVrijeme.takeIf { it.isNotBlank() })
                    dbHelper.addTask(username, updatedTask)
                    tasks.remove(task)
                    tasks.add(updatedTask)
                    editingTask = null
                }) {
                    Text("Spremi")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dbHelper.deleteTask(task)
                    tasks.remove(task)
                    editingTask = null
                }) {
                    Text("Obriši")
                }
            }
        )
    }
}