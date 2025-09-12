package com.india.epilepsyfoundation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.india.epilepsyfoundation.dao.AttackDetailsDao
import com.india.epilepsyfoundation.dao.ContactDao
import com.india.epilepsyfoundation.dao.DoctorVisitReminderDao
import com.india.epilepsyfoundation.dao.MedicationReminderDao
import com.india.epilepsyfoundation.dao.PathologyTestReminderDao
import com.india.epilepsyfoundation.dao.QolieQuestionDao
import com.india.epilepsyfoundation.dao.QuestionnaireDao
import com.india.epilepsyfoundation.dao.RadiologyTestReminderDao
import com.india.epilepsyfoundation.dao.RegisterDao
import com.india.epilepsyfoundation.dao.ReminderNotificationDao
import com.india.epilepsyfoundation.dao.StigmaScaleQuestionDao
import com.india.epilepsyfoundation.dao.WhodasQuestionDao
import com.india.epilepsyfoundation.entity.AttackDetailsEntity
import com.india.epilepsyfoundation.entity.ContactEntity
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity
import com.india.epilepsyfoundation.entity.MedicationReminderEntity
import com.india.epilepsyfoundation.entity.PathologyTestReminderEntity
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.entity.QuestionnaireEntity
import com.india.epilepsyfoundation.entity.RadiologyTestReminderEntity
import com.india.epilepsyfoundation.entity.RegisterEntity
import com.india.epilepsyfoundation.entity.ReminderNotificationEntity
import com.india.epilepsyfoundation.entity.StigmaScaleQuestionEntity
import com.india.epilepsyfoundation.entity.WhodasQuestionEntity

@Database(entities = [RegisterEntity::class, MedicationReminderEntity::class, DoctorVisitReminderEntity::class, PathologyTestReminderEntity::class, RadiologyTestReminderEntity::class, QolieQuestionEntity::class, WhodasQuestionEntity::class, StigmaScaleQuestionEntity::class, QuestionnaireEntity::class, ContactEntity::class, AttackDetailsEntity::class, ReminderNotificationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registerDao(): RegisterDao

    abstract fun medicationReminderDao() : MedicationReminderDao

    abstract fun doctorVisitReminderDao() : DoctorVisitReminderDao

    abstract fun pathologyTestReminderDao() : PathologyTestReminderDao

    abstract fun radiologyTestReminderDao() : RadiologyTestReminderDao

    abstract fun qolieQuestionDao() : QolieQuestionDao

    abstract fun whodasQuestionDao() : WhodasQuestionDao

    abstract fun stigmaScaleQuestionDao() : StigmaScaleQuestionDao

    abstract fun questionnaireDao() : QuestionnaireDao

    abstract fun contactDao() : ContactDao

    abstract fun attackDetailsDao() : AttackDetailsDao

    abstract fun reminderNotificationDao() : ReminderNotificationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "epilepsy_db"
                )
                    .addMigrations(*migrations)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // List of migrations
        val migrations = arrayOf<Migration>()
    }
}
