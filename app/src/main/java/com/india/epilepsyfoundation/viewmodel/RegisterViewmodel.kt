package com.india.epilepsyfoundation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.epilepsyfoundation.entity.RegisterEntity
import com.india.epilepsyfoundation.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewmodel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    fun saveRegisterData(entity: RegisterEntity) {
        viewModelScope.launch {
            authRepository.saveRegistrationData(entity)
        }
    }

}