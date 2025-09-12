package com.india.epilepsyfoundation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.epilepsyfoundation.entity.MedicationReminderEntity
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.entity.StigmaScaleQuestionEntity
import com.india.epilepsyfoundation.entity.WhodasQuestionEntity
import com.india.epilepsyfoundation.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentViewmodel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    fun saveQolieQuestionData(questionEntity: QolieQuestionEntity) {
        viewModelScope.launch {
            try {
                dashboardRepository.saveQolieQuestionData(questionEntity)
                _saveStatus.postValue(true)
            } catch (e: Exception) {
                _saveStatus.postValue(false)
            }
        }
    }

    fun getAllQolieQuestionData(onResult: (List<QolieQuestionEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllQolieQuestionData()
            onResult(list)
        }
    }

    private val _saveWhodasStatus = MutableLiveData<Boolean>()
    val saveWhodasStatus: LiveData<Boolean> get() = _saveWhodasStatus

    fun saveWhodasQuestionData(whodasQuestionEntity: WhodasQuestionEntity) {
        viewModelScope.launch {
            try {
                dashboardRepository.saveWhodasQuestionData(whodasQuestionEntity)
                _saveWhodasStatus.postValue(true)
            } catch (e: Exception) {
                _saveWhodasStatus.postValue(false)
            }
        }
    }

    fun getAllWhodasQuestionData(onResult: (List<WhodasQuestionEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllWhodasQuestionData()
            onResult(list)
        }
    }

    private val _saveStigmaScaleStatus = MutableLiveData<Boolean>()
    val saveStigmaScaleStatus: LiveData<Boolean> get() = _saveStigmaScaleStatus

    fun saveStigmaScaleQuestionData(stigmaScaleQuestionEntity: StigmaScaleQuestionEntity) {
        viewModelScope.launch {
            try {
                dashboardRepository.saveStigmaScaleQuestionData(stigmaScaleQuestionEntity)
                _saveStigmaScaleStatus.postValue(true)
            } catch (e: Exception) {
                _saveStigmaScaleStatus.postValue(false)
            }
        }
    }

    fun getAllStigmaScaleQuestionData(onResult: (List<StigmaScaleQuestionEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllStigmaScaleQuestionData()
            onResult(list)
        }
    }
}
