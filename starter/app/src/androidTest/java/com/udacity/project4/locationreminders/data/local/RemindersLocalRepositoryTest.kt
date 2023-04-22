package com.udacity.project4.locationreminders.data.local

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
    private lateinit var localDataBase: RemindersDatabase

    // Class under test
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    // Executes each task synchronously using Architecture Components.
    // This rule ensures that the test results happen synchronously and in a repeatable order.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        localDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(localDataBase.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        localDataBase.close()
    }

    private fun getReminder(): ReminderDTO {
        return ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 44.3837422503473,
            longitude = 26.11452427588019)
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - A new reminder saved in the database.
        val reminder = getReminder()
        remindersLocalRepository.saveReminder(reminder)

        // WHEN - Reminder retrieved by ID.
        val result = remindersLocalRepository.getReminder(reminder.id)

        // THEN - The same reminder is returned.
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success


        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))
    }

    @Test
    fun deleteAllReminders_getRemindersById() = runBlocking {
        // GIVEN - A new task in the persistent repository.
        val reminder = getReminder()
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()

        // WHEN - Completed in the persistent repository.
        val result = remindersLocalRepository.getReminder(reminder.id)

        // THEN - The task can be retrieved from the persistent repository and is complete.
        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }
}