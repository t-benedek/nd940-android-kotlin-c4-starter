package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

// Use FakeDataSource that acts as a test double to the LocalDataSource
// Data Source is a non nullable mutable list of ReminderData
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(Exception("An Exception occured").localizedMessage)
        } else {
            return Result.Success(ArrayList(reminders))
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("Error")
        } else {
            val reminder = reminders?.find { it.id == id }
            return if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error(Exception("An Exception occured").localizedMessage)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}