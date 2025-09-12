package com.india.epilepsyfoundation.repository

import com.india.epilepsyfoundation.database.AppDatabase
import com.india.epilepsyfoundation.entity.RegisterEntity
import javax.inject.Inject

class AuthRepository @Inject constructor(private val appDatabase: AppDatabase) {

    suspend fun saveRegistrationData(entity: RegisterEntity) {
        appDatabase.registerDao().insert(entity)
    }

    suspend fun getAllRegistrations(): List<RegisterEntity> {
        return appDatabase.registerDao().getAll()
    }

}