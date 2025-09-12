package com.india.epilepsyfoundation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.epilepsyfoundation.entity.ContactEntity
import com.india.epilepsyfoundation.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpDetectorViewModel  @Inject constructor(private val dashboardRepository: DashboardRepository) : ViewModel() {

    private val _isSaved = MutableLiveData<Boolean>()

    val isSaved : LiveData<Boolean> get() = _isSaved

    fun saveContactData(entity: ContactEntity) {
        viewModelScope.launch {
            try {
                dashboardRepository.saveContactData(entity)
                _isSaved.postValue(true)
            } catch (e: Exception) {
                _isSaved.postValue(false)
            }        }
    }
}