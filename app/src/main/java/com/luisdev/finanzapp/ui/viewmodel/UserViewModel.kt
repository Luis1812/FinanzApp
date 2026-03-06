package com.luisdev.finanzapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisdev.finanzapp.data.repository.UserPreferences
import com.luisdev.finanzapp.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserPreferencesRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    // Initial value is null to indicate "loading" from DataStore
    val userPreferences: StateFlow<UserPreferences?> = repository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateName(firstName: String, lastName: String) {
        viewModelScope.launch {
            repository.updateName(firstName, lastName)
        }
    }

    fun updateProfilePicture(path: String) {
        viewModelScope.launch {
            repository.updateProfilePicture(path)
        }
    }

    fun saveProfilePictureLocally(uriString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "profile_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, fileName)

                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }

                // Borramos la foto anterior si existe para no llenar el almacenamiento
                userPreferences.value?.profilePicturePath?.let { oldPath ->
                    val oldFile = File(oldPath)
                    if (oldFile.exists()) oldFile.delete()
                }

                // Guardamos la nueva ruta permanente
                repository.updateProfilePicture(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
