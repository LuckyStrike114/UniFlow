package com.example.uniflow.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.example.uniflow.Task

class TaskRepository private constructor(private val dao: TaskDao) {

    fun getAll(): Flow<List<Task>> = dao.getAll().map { list ->
        list.map { it.toTask() }
    }

    fun getByDate(date: LocalDate): Flow<List<Task>> {
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getByDate(millis).map { tasks -> tasks.map { it.toTask() } }
    }

    suspend fun add(task: Task) {
        dao.insert(task.toEntity())
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRepository? = null

        fun get(context: Context): TaskRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val repo = TaskRepository(db.taskDao())
                INSTANCE = repo
                repo
            }
        }
    }
}

fun TaskEntity.toTask(): Task {
    return Task(vrsta, naziv, androidx.compose.ui.graphics.Color(boja),
        Instant.ofEpochMilli(datum).atZone(ZoneId.systemDefault()).toLocalDate(), vrijeme)
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        vrsta = vrsta,
        naziv = naziv,
        boja = boja.value.toInt(),
        datum = datum.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        vrijeme = vrijeme
    )
}
