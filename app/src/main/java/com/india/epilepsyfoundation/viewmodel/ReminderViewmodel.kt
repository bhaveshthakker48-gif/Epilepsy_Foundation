package com.india.epilepsyfoundation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.epilepsyfoundation.entity.AttackDetailsEntity
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity
import com.india.epilepsyfoundation.entity.MedicationReminderEntity
import com.india.epilepsyfoundation.entity.PathologyTestReminderEntity
import com.india.epilepsyfoundation.entity.RadiologyTestReminderEntity
import com.india.epilepsyfoundation.entity.RegisterEntity
import com.india.epilepsyfoundation.entity.ReminderNotificationEntity
import com.india.epilepsyfoundation.repository.AuthRepository
import com.india.epilepsyfoundation.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReminderViewmodel @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    fun saveReminderMedicationData(
        medicationReminderEntity: MedicationReminderEntity,
        onComplete: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = dashboardRepository.saveReminderMedicationData(medicationReminderEntity)
            onComplete(id.toInt())
        }
    }

    fun updateReminderMedicationData(entity: MedicationReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.updateReminderMedicationData(entity)
            onComplete()
        }
    }

    fun getAllMedicationReminder(onResult: (List<MedicationReminderEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllMedicationReminder()
            onResult(list)
        }
    }

    fun deleteMedicationReminder(medicationReminderEntity: MedicationReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.deleteMedicationReminder(medicationReminderEntity)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun saveReminderDoctorVisitData(
        doctorVisitReminderEntity: DoctorVisitReminderEntity,
        onComplete: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = dashboardRepository.saveReminderDoctorVisitData(doctorVisitReminderEntity)
            onComplete(id.toInt())
        }
    }

    fun updateReminderDoctorVisitData(entity: DoctorVisitReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.updateDoctorVisitReminder(entity)
            onComplete()
        }
    }

    fun getAllDoctorVisitReminder(onResult: (List<DoctorVisitReminderEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllDoctorVisitReminder()
            onResult(list)
        }
    }

    fun deleteDoctorVisitReminder(visit: DoctorVisitReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.deleteDoctorVisitReminder(visit)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }


    fun saveReminderPathologyTestData(
        pathologyTestReminderEntity: PathologyTestReminderEntity,
        onComplete: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            val newId = dashboardRepository.saveReminderPathologyTestData(pathologyTestReminderEntity)
            onComplete(newId.toInt())
        }
    }


    fun updatePathologyTestData(entity: PathologyTestReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.updatePathologyTestReminder(entity)
            onComplete()
        }
    }

    fun getAllPathologyTestReminder(onResult: (List<PathologyTestReminderEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllPathologyTestReminder()
            onResult(list)
        }
    }

    fun deletePathologyTestReminder(pathologyTestReminderEntity: PathologyTestReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.deletePathologyTestReminder(pathologyTestReminderEntity)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun saveReminderRadiologyTestData(
        radiologyTestReminderEntity: RadiologyTestReminderEntity,
        onComplete: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            val newId = dashboardRepository.saveReminderRadiologyTestData(radiologyTestReminderEntity)
            onComplete(newId.toInt())
        }
    }

    fun updateRadiologyTestData(entity: RadiologyTestReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.updateRadiologyTestData(entity)
            onComplete()
        }
    }

    fun getAllRadiologyTestReminder(onResult: (List<RadiologyTestReminderEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllRadiologyTestReminder()
            onResult(list)
        }
    }

    fun deleteRadiologyTestReminder(radiologyTestReminderEntity: RadiologyTestReminderEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            dashboardRepository.deleteRadiologyTestReminder(radiologyTestReminderEntity)
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun loadAllReminders(onResult: (List<ReminderNotificationEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.loadAllReminders()
            onResult(list)
        }
    }
}