package com.example.uniflow

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.example.uniflow.data.AppDatabase
import com.example.uniflow.data.TaskEntity
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId

class TaskDaoTest {
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadTask() = runBlocking {
        val dao = db.taskDao()
        val date = LocalDate.of(2025,1,1)
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val entity = TaskEntity(vrsta="Predavanje", naziv="Kotlin", boja=0xff0000, datum=millis, vrijeme=null)
        dao.insert(entity)
        val tasks = dao.getAll().first()
        assertEquals(1, tasks.size)
        assertEquals("Kotlin", tasks.first().naziv)
    }
}
