package com.example.uniflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.uniflow.ui.theme.UniFlowTheme
import com.example.uniflow.data.TaskRepository
import com.example.uniflow.TasksActivity



class MainActivity : ComponentActivity() {

    private var isDarkThemeState = mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        // Ažurira temu pri povratku
        val prefs = getSharedPreferences("uniflow_prefs", MODE_PRIVATE)
        isDarkThemeState.value = prefs.getBoolean("dark_mode", false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("uniflow_prefs", MODE_PRIVATE)
        isDarkThemeState.value = prefs.getBoolean("dark_mode", false)

        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Student"

        setContent {
            UniFlowTheme(darkTheme = isDarkThemeState.value) {
                MainScreen(username = username)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(username: String) {
    val context = LocalContext.current
    val repo = remember { TaskRepository.get(context) }
    val taskList by repo.getAll().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Izbornik", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()

                NavigationDrawerItem(label = { Text("Kalendar") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                })

                NavigationDrawerItem(label = { Text("Obaveze") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, TasksActivity::class.java))
                })

                NavigationDrawerItem(label = { Text("Postavke") }, selected = false, onClick = {
                    scope.launch { drawerState.close() }
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                })

                NavigationDrawerItem(label = { Text("Pomoć") }, selected = false, onClick = {})
                NavigationDrawerItem(label = { Text("O nama") }, selected = false, onClick = {})
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("UniFlow", fontWeight = FontWeight.Bold, color = Color.White)
                                Text(username, color = Color.White)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF31E981),
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                },
                floatingActionButton = { // ✅ Ovdje je sad ispravno
                    FloatingActionButton(
                        onClick = { showDialog = true },
                        containerColor = Color(0xFF31E981),
                        contentColor = Color.White
                    ) {
                        Text("+")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    FunctionalCalendar(taskList) { selectedDay = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    selectedDay?.let { day ->
                        Text("Obaveze za ${day.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}:", fontWeight = FontWeight.Bold)
                        taskList.filter { it.datum == day }.forEach { task ->
                            val info = buildString {
                                append("${task.vrsta}: ${task.naziv}")
                                task.vrijeme?.let { append(" - $it") }
                            }
                            Text(info, color = task.boja)
                        }
                    }
                }

                if (showDialog) {
                    AddTaskDialog(
                        onDismiss = { showDialog = false },
                        onSave = { task ->
                            scope.launch { repo.add(task) }
                            showDialog = false
                        }
                    )
                }
            }
        }
    )
}


@Composable
fun FunctionalCalendar(taskList: List<Task>, onDateSelected: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val markedDates = taskList.groupBy { it.datum }.mapValues { it.value.first().boja }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Text("<")
            }

            Text(
                "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("hr"))} ${currentMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Text(">")
            }
        }

        val daysOfWeek = listOf("Pon", "Uto", "Sri", "Čet", "Pet", "Sub", "Ned")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            daysOfWeek.forEach { day ->
                Text(day, fontWeight = FontWeight.SemiBold)
            }
        }

        val firstDayOfMonth = (currentMonth.atDay(1).dayOfWeek.value % 7 + 6) % 7
        val daysInMonth = currentMonth.lengthOfMonth()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            items(firstDayOfMonth) { Box(modifier = Modifier.size(40.dp)) }
            items((1..daysInMonth).toList()) { day ->
                val date = currentMonth.atDay(day)
                val color = markedDates[date] ?: Color.Transparent
                val textColor = if (color != Color.Transparent) {
                    if (color.luminance() > 0.5f) Color.Black else Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(color)
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        color = textColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onSave: (Task) -> Unit) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color(0xFF31E981)) }
    var taskType by remember { mutableStateOf("") }
    var taskName by remember { mutableStateOf("") }
    var taskTime by remember { mutableStateOf("") }

    val selectedDate: LocalDate? = datePickerState.selectedDateMillis?.let {
        java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj obavezu") },
        text = {
            Column {
                Text("Datum: ${selectedDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "nije odabran"}")
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { showDatePicker = true }) {
                    Text("Odaberi datum")
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(taskType, { taskType = it }, label = { Text("Vrsta obaveze") })
                OutlinedTextField(taskName, { taskName = it }, label = { Text("Naziv obaveze") })
                OutlinedTextField(taskTime, { taskTime = it }, label = { Text("Vrijeme (opcionalno)") })

                Spacer(modifier = Modifier.height(16.dp))
                Text("Boja:")
                Row {
                    listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow).forEach { color ->
                        Box(
                            Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(color)
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedDate != null && taskType.isNotBlank() && taskName.isNotBlank()) {
                    onSave(
                        Task(
                            vrsta = taskType,
                            naziv = taskName,
                            boja = selectedColor,
                            datum = selectedDate,
                            vrijeme = taskTime.ifBlank { null }
                        )
                    )
                }
            }) {
                Text("Spremi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Odustani")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Potvrdi")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
