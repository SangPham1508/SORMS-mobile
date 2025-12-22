package com.example.sorms_app.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.UpdateProfileRequest
import com.example.sorms_app.data.datasource.remote.UserApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class UserVerificationUiState(
    val isLoading: Boolean = false,
    // Personal info
    val fullName: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val address: String = "",
    // CCCD info
    val idCardNumber: String = "",
    val idCardIssueDate: String = "",
    val idCardIssuePlace: String = "",
    // Images
    val idCardFrontUri: Uri? = null,
    val idCardBackUri: Uri? = null,
    val faceFrontUri: Uri? = null,
    val faceLeftUri: Uri? = null,
    val faceRightUri: Uri? = null,
    // Status
    val isProfileSaved: Boolean = false,
    val isFaceRegistered: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // Existing data loaded
    val isExistingDataLoaded: Boolean = false
)

@HiltViewModel
class UserVerificationViewModel @Inject constructor(
    private val userApiService: UserApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserVerificationUiState())
    val uiState: StateFlow<UserVerificationUiState> = _uiState.asStateFlow()

    init {
        // Delay loading to avoid ANR
        viewModelScope.launch {
            kotlinx.coroutines.delay(200)
            loadExistingProfile()
        }
    }

    private fun loadExistingProfile() {
        viewModelScope.launch {
            val userId = AuthSession.accountId ?: run {
                _uiState.update { it.copy(isExistingDataLoaded = true) }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val response = userApiService.getUserProfile(userId)
                if (response.isSuccessful) {
                    val profile = response.body()?.data
                    if (profile != null) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                fullName = profile.fullName ?: "",
                                phoneNumber = profile.phoneNumber ?: "",
                                dateOfBirth = profile.dateOfBirth ?: "",
                                gender = profile.gender ?: "",
                                address = profile.address ?: "",
                                idCardNumber = profile.idCardNumber ?: "",
                                idCardIssueDate = profile.idCardIssueDate ?: "",
                                idCardIssuePlace = profile.idCardIssuePlace ?: "",
                                isExistingDataLoaded = true
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, isExistingDataLoaded = true) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, isExistingDataLoaded = true) }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        isExistingDataLoaded = true,
                        errorMessage = "Không thể tải thông tin: ${e.message}"
                    )
                }
            }
        }
    }

    // Update form fields
    fun updateFullName(value: String) { _uiState.update { it.copy(fullName = value) } }
    fun updatePhoneNumber(value: String) { _uiState.update { it.copy(phoneNumber = value) } }
    fun updateDateOfBirth(value: String) { _uiState.update { it.copy(dateOfBirth = value) } }
    fun updateGender(value: String) { _uiState.update { it.copy(gender = value) } }
    fun updateAddress(value: String) { _uiState.update { it.copy(address = value) } }
    fun updateIdCardNumber(value: String) { _uiState.update { it.copy(idCardNumber = value) } }
    fun updateIdCardIssueDate(value: String) { _uiState.update { it.copy(idCardIssueDate = value) } }
    fun updateIdCardIssuePlace(value: String) { _uiState.update { it.copy(idCardIssuePlace = value) } }

    // Update image URIs
    fun updateIdCardFrontUri(uri: Uri?) { _uiState.update { it.copy(idCardFrontUri = uri) } }
    fun updateIdCardBackUri(uri: Uri?) { _uiState.update { it.copy(idCardBackUri = uri) } }
    fun updateFaceFrontUri(uri: Uri?) { _uiState.update { it.copy(faceFrontUri = uri) } }
    fun updateFaceLeftUri(uri: Uri?) { _uiState.update { it.copy(faceLeftUri = uri) } }
    fun updateFaceRightUri(uri: Uri?) { _uiState.update { it.copy(faceRightUri = uri) } }

    fun saveProfile() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Validate required fields
            if (state.fullName.isBlank() || state.phoneNumber.isBlank() || state.idCardNumber.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin bắt buộc") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val request = UpdateProfileRequest(
                    fullName = state.fullName,
                    phoneNumber = state.phoneNumber,
                    firstName = state.fullName.split(" ").lastOrNull(),
                    lastName = state.fullName.split(" ").dropLast(1).joinToString(" "),
                    dateOfBirth = state.dateOfBirth.takeIf { it.isNotBlank() },
                    gender = state.gender.takeIf { it.isNotBlank() },
                    address = state.address.takeIf { it.isNotBlank() },
                    city = null,
                    state = null,
                    postalCode = null,
                    country = "Vietnam",
                    avatarUrl = null,
                    emergencyContactName = null,
                    emergencyContactPhone = null,
                    emergencyContactRelationship = null,
                    idCardNumber = state.idCardNumber,
                    idCardIssueDate = state.idCardIssueDate.takeIf { it.isNotBlank() },
                    idCardIssuePlace = state.idCardIssuePlace.takeIf { it.isNotBlank() }
                )

                val response = userApiService.updateProfile(request)
                
                if (response.isSuccessful) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isProfileSaved = true,
                            successMessage = "Đã lưu thông tin cá nhân"
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Không thể lưu thông tin: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Lỗi: ${e.message}"
                    )
                }
            }
        }
    }

    fun registerFace(context: Context) {
        viewModelScope.launch {
            val state = _uiState.value
            val userId = AuthSession.accountId
            
            if (userId == null) {
                _uiState.update { it.copy(errorMessage = "Bạn cần đăng nhập") }
                return@launch
            }

            // Validate images
            if (state.faceFrontUri == null || state.faceLeftUri == null || state.faceRightUri == null) {
                _uiState.update { it.copy(errorMessage = "Vui lòng chụp đủ 3 ảnh khuôn mặt") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val faceImages = mutableListOf<MultipartBody.Part>()
                
                // Convert URIs to MultipartBody.Part
                listOf(
                    state.faceFrontUri to "front",
                    state.faceLeftUri to "left",
                    state.faceRightUri to "right"
                ).forEach { (uri, name) ->
                    val file = uriToFile(context, uri!!, "face_$name.jpg")
                    val requestBody = file.asRequestBody("image/jpeg".toMediaType())
                    faceImages.add(MultipartBody.Part.createFormData("faceImages", file.name, requestBody))
                }

                val userIdBody = userId.toRequestBody("text/plain".toMediaType())
                val nameBody = (state.fullName.ifBlank { "User" }).toRequestBody("text/plain".toMediaType())

                val response = userApiService.registerFace(userIdBody, nameBody, faceImages)

                if (response.isSuccessful && response.body()?.data?.success == true) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isFaceRegistered = true,
                            successMessage = "Đã đăng ký khuôn mặt thành công"
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Không thể đăng ký khuôn mặt: ${response.body()?.data?.message ?: response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Lỗi: ${e.message}"
                    )
                }
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri, fileName: String): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun isVerificationComplete(): Boolean {
        val state = _uiState.value
        return state.isProfileSaved && state.isFaceRegistered
    }

    fun isProfileComplete(): Boolean {
        val state = _uiState.value
        return state.fullName.isNotBlank() && 
               state.phoneNumber.isNotBlank() && 
               state.idCardNumber.isNotBlank()
    }
}

