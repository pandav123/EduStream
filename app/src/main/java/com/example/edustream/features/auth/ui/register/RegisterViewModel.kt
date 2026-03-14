package com.example.edustream.features.auth.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.auth.domain.repository.AuthRepository
import com.example.edustream.ui.common.UiState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<UiState<FirebaseUser>?>(null)
    val registerState = _registerState.asStateFlow()

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            val result = authRepository.register(email, password, name)
            _registerState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        _registerState.value = null
    }
}
