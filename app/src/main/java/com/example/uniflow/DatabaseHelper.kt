package com.example.uniflow

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import java.time.LocalDate

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        val createTasksTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK_DATE TEXT,
                $COLUMN_TASK_COLOR TEXT,
                $COLUMN_TASK_VRSTA TEXT,
                $COLUMN_TASK_NAZIV TEXT,
                $COLUMN_TASK_VRIJEME TEXT,
                $COLUMN_TASK_USER TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createTasksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun registerUser(username: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        return try {
            db.insertOrThrow(TABLE_USERS, null, values)
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun addTask(username: String, task: Task): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK_DATE, task.datum.toString())
            put(COLUMN_TASK_COLOR, "#%06X".format(0xFFFFFF and task.boja.hashCode()))
            put(COLUMN_TASK_VRSTA, task.vrsta)
            put(COLUMN_TASK_NAZIV, task.naziv)
            put(COLUMN_TASK_VRIJEME, task.vrijeme ?: "")
            put(COLUMN_TASK_USER, username)
        }
        val result = db.insert(TABLE_TASKS, null, values)
        db.close()
        return result != -1L
    }

    fun getTasksForUser(username: String): List<Task> {
        val db = readableDatabase
        val taskList = mutableListOf<Task>()
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_TASK_USER = ?",
            arrayOf(username),
            null,
            null,
            "$COLUMN_TASK_DATE ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val dateStr = getString(getColumnIndexOrThrow(COLUMN_TASK_DATE))
                val colorStr = getString(getColumnIndexOrThrow(COLUMN_TASK_COLOR))
                val vrsta = getString(getColumnIndexOrThrow(COLUMN_TASK_VRSTA))
                val naziv = getString(getColumnIndexOrThrow(COLUMN_TASK_NAZIV))
                val vrijeme = getString(getColumnIndexOrThrow(COLUMN_TASK_VRIJEME))

                val color = Color(colorStr.toColorInt())
                val date = LocalDate.parse(dateStr)
                val task = Task(vrsta, naziv, color, date, if (vrijeme.isBlank()) null else vrijeme)
                taskList.add(task)
            }
        }

        cursor.close()
        db.close()
        return taskList
    }

    fun deleteTask(task: Task): Boolean {
        val db = writableDatabase
        val result = db.delete(
            "tasks",
            "date = ? AND color = ? AND vrsta = ? AND naziv = ? AND (vrijeme IS ? OR vrijeme = ?)",
            arrayOf(
                task.datum.toString(),
                "#%06X".format(0xFFFFFF and task.boja.hashCode()),
                task.vrsta,
                task.naziv,
                task.vrijeme,
                task.vrijeme ?: ""
            )
        )
        db.close()
        return result > 0
    }

    fun deleteAllTasksForUser(username: String): Boolean {
        val db = writableDatabase
        val result = db.delete("tasks", "user = ?", arrayOf(username))
        db.close()
        return result > 0
    }


    companion object {
        private const val DB_NAME = "uniflow.db"
        private const val DB_VERSION = 3

        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_TASK_ID = "task_id"
        private const val COLUMN_TASK_DATE = "date"
        private const val COLUMN_TASK_COLOR = "color"
        private const val COLUMN_TASK_VRSTA = "vrsta"
        private const val COLUMN_TASK_NAZIV = "naziv"
        private const val COLUMN_TASK_VRIJEME = "vrijeme"
        private const val COLUMN_TASK_USER = "user"
    }
}

