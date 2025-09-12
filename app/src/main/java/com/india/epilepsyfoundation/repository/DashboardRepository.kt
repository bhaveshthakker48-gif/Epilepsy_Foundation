package com.india.epilepsyfoundation.repository

import com.india.epilepsyfoundation.dao.ContactDao
import com.india.epilepsyfoundation.database.AppDatabase
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
import javax.inject.Inject

class DashboardRepository  @Inject constructor(private val appDatabase: AppDatabase)  {

    // Remember ViewModel
    suspend fun saveReminderMedicationData(medicationReminderEntity: MedicationReminderEntity) : Long{
        return appDatabase.medicationReminderDao().saveReminderMedicationData(medicationReminderEntity)
    }

    suspend fun updateReminderMedicationData(entity: MedicationReminderEntity) {
        appDatabase.medicationReminderDao().updateReminderMedicationData(entity)
    }

    suspend fun getAllMedicationReminder(): List<MedicationReminderEntity> {
        return appDatabase.medicationReminderDao().getAllMedicationReminder()
    }

    suspend fun deleteMedicationReminder(medicationReminderEntity: MedicationReminderEntity) {
        appDatabase.medicationReminderDao().deleteMedicationReminder(medicationReminderEntity)
    }

    suspend fun saveReminderDoctorVisitData(entity: DoctorVisitReminderEntity): Long {
        return appDatabase.doctorVisitReminderDao().saveReminderDoctorVisitData(entity)
    }

    suspend fun updateDoctorVisitReminder(entity: DoctorVisitReminderEntity) {
        appDatabase.doctorVisitReminderDao().updateDoctorVisitReminder(entity)
    }

    suspend fun getAllDoctorVisitReminder(): List<DoctorVisitReminderEntity> {
        return appDatabase.doctorVisitReminderDao().getAllDoctorVisitReminder()
    }

    suspend fun deleteDoctorVisitReminder(visit: DoctorVisitReminderEntity) {
        appDatabase.doctorVisitReminderDao().deleteDoctorVisitReminder(visit)
    }

    suspend fun saveReminderPathologyTestData(pathologyTestReminderEntity: PathologyTestReminderEntity): Long {
        return appDatabase.pathologyTestReminderDao().saveReminderPathologyTestData(pathologyTestReminderEntity)
    }

    suspend fun updatePathologyTestReminder(entity: PathologyTestReminderEntity) {
        appDatabase.pathologyTestReminderDao().updatePathologyTestReminder(entity)
    }

    suspend fun getAllPathologyTestReminder(): List<PathologyTestReminderEntity> {
        return appDatabase.pathologyTestReminderDao().getAllPathologyTestReminder()
    }

    suspend fun deletePathologyTestReminder(pathologyTestReminderEntity: PathologyTestReminderEntity) {
        appDatabase.pathologyTestReminderDao().deletePathologyTestReminder(pathologyTestReminderEntity)
    }

    suspend fun saveReminderRadiologyTestData(radiologyTestReminderEntity: RadiologyTestReminderEntity) : Long {
        return appDatabase.radiologyTestReminderDao().saveReminderRadiologyTestData(radiologyTestReminderEntity)
    }

    suspend fun updateRadiologyTestData(entity: RadiologyTestReminderEntity) {
        appDatabase.radiologyTestReminderDao().updateRadiologyTestData(entity)
    }


    suspend fun getAllRadiologyTestReminder(): List<RadiologyTestReminderEntity> {
        return appDatabase.radiologyTestReminderDao().getAllRadiologyTestReminder()
    }

    suspend fun deleteRadiologyTestReminder(radiologyTestReminderEntity: RadiologyTestReminderEntity) {
        appDatabase.radiologyTestReminderDao().deleteRadiologyTestReminder(radiologyTestReminderEntity)
    }

    suspend fun loadAllReminders(): List<ReminderNotificationEntity> {
        return appDatabase.reminderNotificationDao().getAll()
    }

    // Assessment ViewModel
    suspend fun saveQolieQuestionData(questionEntity: QolieQuestionEntity) {
        appDatabase.qolieQuestionDao().saveQolieQuestionData(questionEntity)
    }

    suspend fun getAllQolieQuestionData(): List<QolieQuestionEntity> {
        return appDatabase.qolieQuestionDao().getAllQolieQuestion()
    }


    suspend fun saveWhodasQuestionData(whodasQuestionEntity: WhodasQuestionEntity) {
        appDatabase.whodasQuestionDao().saveWhodasQuestionData(whodasQuestionEntity)
    }

    suspend fun getAllWhodasQuestionData(): List<WhodasQuestionEntity> {
        return appDatabase.whodasQuestionDao().getAllWhodasQuestion()
    }

    suspend fun saveStigmaScaleQuestionData(entity: StigmaScaleQuestionEntity) {
        appDatabase.stigmaScaleQuestionDao().saveStigmaScaleQuestionData(entity)
    }

    suspend fun getAllStigmaScaleQuestionData(): List<StigmaScaleQuestionEntity> {
        return appDatabase.stigmaScaleQuestionDao().getAllStigmaScaleQuestion()
    }

    // Qu
    suspend fun saveQuestionnaireData(questionnaireEntity: QuestionnaireEntity) {
        appDatabase.questionnaireDao().saveQuestionnaireData(questionnaireEntity)
    }

    suspend fun getAllQuestionnaireData(): List<QuestionnaireEntity> {
        return appDatabase.questionnaireDao().getAllQuestionnaireData()
    }

    //ContactViewModel
    suspend fun saveContactData(entity: ContactEntity) {
        appDatabase.contactDao().insert(entity)
    }

    //Attack Details
    suspend fun saveAttackDetailsData(attackDetailsEntity: AttackDetailsEntity) {
        appDatabase.attackDetailsDao().saveAttackDetailsData(attackDetailsEntity)
    }

    suspend fun getAllAttackDetailsData(): List<AttackDetailsEntity> {
        return appDatabase.attackDetailsDao().getAllAttackDetailsData()
    }


}