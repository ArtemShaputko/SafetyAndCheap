package com.example.safetyandcheap.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetyandcheap.data.repository.FirebaseStorageRepository
import com.example.safetyandcheap.service.MainApiService
import com.example.safetyandcheap.service.dto.property.Announcement
import com.example.safetyandcheap.service.dto.search.AnnouncementSearchDto
import com.example.safetyandcheap.service.dto.search.ApartmentSearchDto
import com.example.safetyandcheap.service.dto.search.HouseSearchDto
import com.example.safetyandcheap.service.dto.search.OfferSearchDto
import com.example.safetyandcheap.service.dto.search.RoomSearchDto
import com.example.safetyandcheap.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val mainApi: MainApiService,
    private val storageRepo: FirebaseStorageRepository
) : ViewModel() {
    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState> = _uiState

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _currentAnnouncement = MutableStateFlow<Announcement?>(null)
    val currentAnnouncement: StateFlow<Announcement?> = _currentAnnouncement.asStateFlow()

    private val _images = MutableStateFlow<Map<Long, List<String>>>(emptyMap())
    val images: StateFlow<Map<Long, List<String>>> = _images.asStateFlow()

    private val _cart = MutableStateFlow<List<Announcement>?>(null)
    val cart: StateFlow<List<Announcement>?> = _cart.asStateFlow()


    private val _searchFilters = MutableStateFlow(
        AnnouncementSearchDto()
    )
    val searchFilters: StateFlow<AnnouncementSearchDto> = _searchFilters.asStateFlow()

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val response = mainApi.searchAnnouncement(_searchFilters.value)
                if (response.code() != 200) {
                    Log.e("Network error", response.message())
                    _uiState.value = UiState.NetworkError("Network error")
                } else {
                    _announcements.value = response.body() ?: emptyList()
                    _uiState.value = UiState.Idle
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.NetworkError("Network error")
                _announcements.value = emptyList()
                _images.value = emptyMap()
            }
        }
    }

    fun loadImagesForAnnouncement(announcementId: Long, photoUrls: List<String>) {
        viewModelScope.launch {
            try {
                if (_images.value[announcementId] == null || _images.value[announcementId]?.isEmpty() == true) {
                    val imageUrls = storageRepo.downloadImages(photoUrls)
                    _images.update { currentMap ->
                        currentMap.toMutableMap().apply { put(announcementId, imageUrls) }
                    }
                    Log.d("Announcement $announcementId", "Announcement updated")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _images.update { currentMap ->
                    currentMap.toMutableMap().apply { put(announcementId, emptyList()) }
                }
            }
        }
    }

    fun getCart() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val response = mainApi.getCart()
                if(response.code() == 200) {
                    _cart.value = response.body()
                    _uiState.value = UiState.Idle
                } else {
                    _cart.value = null
                    _uiState.value = UiState.NetworkError("Network error")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _cart.value = null
                _uiState.value = UiState.NetworkError("Network error")
            }
        }
    }

    fun setCurrentAnnouncement(announcement: Announcement) {
        _currentAnnouncement.value = announcement
    }

    fun updateFilters(newFilters: AnnouncementSearchDto) {
        _searchFilters.value = newFilters
        loadData()
    }

    fun moveToCart(announcement: Announcement?) {
        if(announcement != null) {
            viewModelScope.launch {
                try {
                    _uiState.value = UiState.Loading
                    val response = mainApi.moveToCart(announcement.id)
                    if (response.code() != 200) {
                        Log.e("Network error", response.message())
                        _uiState.value = UiState.NetworkError("Network error")
                    } else {
                        _uiState.value = UiState.Idle
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = UiState.NetworkError("Network error")
                    _announcements.value = emptyList()
                    _images.value = emptyMap()
                }
                getCart()
            }
        }
    }

    fun removeFromCart(announcement: Announcement?) {
        if(announcement != null) {
            viewModelScope.launch {
                try {
                    _uiState.value = UiState.Loading
                    val response = mainApi.removeFromCart(announcement.id)
                    if (response.code() != 200) {
                        Log.e("Network error", response.message())
                        _uiState.value = UiState.NetworkError("Network error")
                    } else {
                        _uiState.value = UiState.Idle
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = UiState.NetworkError("Network error")
                    _announcements.value = emptyList()
                    _images.value = emptyMap()
                }
                getCart()
            }
        }
    }

    fun resetFilters() {
        _searchFilters.value = AnnouncementSearchDto(
            property = when (_searchFilters.value.property) {
                is HouseSearchDto -> HouseSearchDto()
                is ApartmentSearchDto -> ApartmentSearchDto()
                is RoomSearchDto -> RoomSearchDto()
                else -> null
            },
            offer = OfferSearchDto()
        )
        loadData()
    }
}