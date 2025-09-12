package com.india.epilepsyfoundation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.epilepsyfoundation.entity.AttackDetailsEntity
import com.india.epilepsyfoundation.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttackDetailsViewModel  @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    fun saveAttackDetailsData(attackDetailsEntity: AttackDetailsEntity) {
        viewModelScope.launch {
            try {
                dashboardRepository.saveAttackDetailsData(attackDetailsEntity)
                _saveStatus.postValue(true)
            } catch (e: Exception) {
                _saveStatus.postValue(false)
            }
        }
    }

    fun getAllAttackDetailsData(onResult: (List<AttackDetailsEntity>) -> Unit) {
        viewModelScope.launch {
            val list = dashboardRepository.getAllAttackDetailsData()
            onResult(list)
        }
    }


}