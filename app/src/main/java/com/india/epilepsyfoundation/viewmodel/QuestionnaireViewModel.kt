package com.india.epilepsyfoundation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.entity.QuestionnaireEntity
import com.india.epilepsyfoundation.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    fun saveQolieQuestionData(questionnaireEntity: QuestionnaireEntity) {
        viewModelScope.launch {
            try {
                dashboardRepository.saveQuestionnaireData(questionnaireEntity)
                _saveStatus.postValue(true)
            } catch (e: Exception) {
                _saveStatus.postValue(false)
            }
        }
    }

    fun getAllQuestionnaireData(onResult: (List<QuestionnaireEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllQuestionnaireData()
            onResult(list)
        }
    }


}