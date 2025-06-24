package com.example.uniflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.example.uniflow.data.TaskRepository
import com.example.uniflow.ui.theme.UniFlowTheme
import java.time.format.DateTimeFormatter

class TasksActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = TaskRepository.get(this)

        setContent {
            UniFlowTheme {
                val tasks by repo.getAll().collectAsState(initial = emptyList())
                Scaffold(topBar = {
                    TopAppBar(title = { Text("Sve obaveze") })
                }) { padding ->
                    Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                        tasks.forEach { task ->
                            val textColor = if (task.boja.luminance() > 0.8f) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                task.boja
                            }
                            Text(
                                "${task.vrsta}: ${task.naziv} - " +
                                        task.datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { /* future details */ },
                                color = textColor
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
