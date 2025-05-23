package com.example.safetyandcheap.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetyandcheap.data.repository.FirebaseStorageRepository
import com.example.safetyandcheap.service.CurrentUserState
import com.example.safetyandcheap.service.DealType
import com.example.safetyandcheap.service.MainApiService
import com.example.safetyandcheap.service.PropertyType
import com.example.safetyandcheap.service.dto.InfrastructureType
import com.example.safetyandcheap.service.dto.StatusType
import com.example.safetyandcheap.service.dto.User
import com.example.safetyandcheap.service.dto.property.Announcement
import com.example.safetyandcheap.service.dto.property.Apartment
import com.example.safetyandcheap.service.dto.property.House
import com.example.safetyandcheap.service.dto.property.Image
import com.example.safetyandcheap.service.dto.property.Offer
import com.example.safetyandcheap.service.dto.property.Property
import com.example.safetyandcheap.service.dto.property.Room
import com.example.safetyandcheap.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri

sealed class ImageSource(open val uri: Uri) {
    data class Local(override val uri: Uri) : ImageSource(uri)
    data class Remote(
        val image: Image,
        override val uri: Uri
    ) : ImageSource(uri)

    fun isLocal() = this is Local
    fun isRemote() = this is Remote
}

@HiltViewModel
class AddAnnouncementViewModel @Inject constructor(
    private val mainApi: MainApiService,
    private val storageRepository: FirebaseStorageRepository,
    private val currentUserState: CurrentUserState,
    private val storageRepo: FirebaseStorageRepository
) : ViewModel() {

    private val _allImageSources = mutableStateListOf<ImageSource>()
    val allImageSources: List<ImageSource> get() = _allImageSources

    private val _usersAnnouncements = MutableStateFlow<List<Announcement>?>(null)
    val usersAnnouncements: StateFlow<List<Announcement>?> = _usersAnnouncements.asStateFlow()

    private val _loadedImages =
        MutableStateFlow<Map<Long, List<Image>>>(emptyMap()) // Изменим тип на List<Image>
    val loadedImages: StateFlow<Map<Long, List<Image>>> = _loadedImages.asStateFlow()

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentAnnouncement = MutableStateFlow<Announcement?>(null)
    val currentAnnouncement: StateFlow<Announcement?> = _currentAnnouncement.asStateFlow()

    private var user: User? = null

    var address by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var price by mutableStateOf("")
        private set
    var negotiablePrice by mutableStateOf(false)
        private set
    var totalArea by mutableStateOf("")
        private set
    var selectedInfrastructure by mutableStateOf<Set<String>>(setOf())
        private set

    // House-specific properties
    var floorsNum by mutableStateOf("")
        private set
    var bedroomsNum by mutableStateOf<Int?>(null)
        private set
    var siteArea by mutableStateOf("")
        private set
    var selectedEarthStatus by mutableStateOf<String?>(null)
        private set

    // Apartment-specific properties
    var roomsNum by mutableStateOf<Int?>(null)
        private set
    var livingArea by mutableStateOf("")
        private set
    var floor by mutableStateOf("")
        private set

    // Room-specific properties
    var roomsNumInOffer by mutableStateOf<Int?>(null)
        private set
    var roomsNumInApartment by mutableStateOf<Int?>(null)
        private set

    // Navigation state
    var selectedPropertyType by mutableStateOf<PropertyType?>(null)
        private set

    // Navigation state
    var selectedDealType by mutableStateOf<DealType?>(null)
        private set

    fun updateAddress(newValue: String) {
        address = newValue
    }

    fun updateDescription(newValue: String) {
        description = newValue
    }

    fun updatePrice(newValue: String) {
        price = newValue
    }

    fun updateNegotiablePrice(newValue: Boolean) {
        negotiablePrice = newValue
    }

    fun updateTotalArea(newValue: String) {
        totalArea = newValue
    }

    fun updateInfrastructure(infrastructure: Set<String>) {
        selectedInfrastructure = infrastructure
    }

    // House methods
    fun updateFloorsNum(newValue: String) {
        floorsNum = newValue
    }

    fun updateBedroomsNum(newValue: Int?) {
        bedroomsNum = newValue
    }

    fun updateSiteArea(newValue: String) {
        siteArea = newValue
    }

    fun updateEarthType(newValue: String?) {
        selectedEarthStatus = newValue
    }

    // Apartment methods
    fun updateRoomsNum(newValue: Int?) {
        roomsNum = newValue
    }

    fun updateLivingArea(newValue: String) {
        livingArea = newValue
    }

    fun updateFloor(newValue: String) {
        floor = newValue
    }

    // Room methods
    fun updateRoomsInOffer(newValue: Int?) {
        roomsNumInOffer = newValue
    }

    fun updateRoomsInApartment(newValue: Int?) {
        roomsNumInApartment = newValue
    }

    fun selectPropertyType(type: PropertyType) {
        selectedPropertyType = type
    }

    fun selectDealType(type: DealType) {
        selectedDealType = type
    }

    fun resetState() {
        _uiState.value = UiState.Idle
        _allImageSources.clear()
        address = ""
        description = ""
        price = ""
        negotiablePrice = false
        totalArea = ""
        selectedInfrastructure = setOf()

        // Reset house-specific
        floorsNum = ""
        bedroomsNum = null
        siteArea = ""
        selectedEarthStatus = null

        // Reset apartment-specific
        roomsNum = null
        livingArea = ""
        floor = ""

        // Reset room-specific
        roomsNumInOffer = null
        roomsNumInApartment = null
    }

    fun setCurrentAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            _uiState.value = UiState.Idle
            _allImageSources.clear()
            val property = announcement.property

            val imageList = loadedImages.value[announcement.id] ?: emptyList()
            _allImageSources.addAll(property.photos.zip(imageList).map { (remote, local) ->
                ImageSource.Remote(remote, local.url.toUri())
            })

            address = property.address
            description = announcement.description
            price = announcement.offer.price.toString()
            negotiablePrice = property.bargain
            totalArea = property.area.toString()
            floorsNum = property.floors.toString()
            selectedInfrastructure = property.infrastructure.map { it.type }.toSet()

            when (property) {
                is Apartment -> {
                    roomsNum = property.rooms
                    livingArea = property.livingArea.toString()
                    floor = property.floor.toString()
                }

                is House -> {
                    bedroomsNum = property.bedrooms
                    siteArea = property.siteArea.toString()
                    selectedEarthStatus = property.earthStatus
                }

                is Room -> {
                    roomsNumInOffer = property.roomsInOffer
                    roomsNumInApartment = property.roomsInApartment
                    livingArea = property.livingArea.toString()
                    floor = property.floor.toString()
                }
            }
            _currentAnnouncement.value = announcement
        }
    }

    fun addLocalImages(newUris: List<Uri>) {
        _allImageSources.addAll(newUris.map { ImageSource.Local(it) })
    }

    fun removeImage(index: Int) {
        if (index < _allImageSources.size) {
            val imageSource = _allImageSources[index]
            when (imageSource) {
                is ImageSource.Local -> {
                    _allImageSources.removeAt(index)
                }

                is ImageSource.Remote -> {
                    _allImageSources.removeAt(index)
                }
            }
        }
    }

    fun deleteAnnouncement(announcementId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val response = mainApi.deleteAnnouncement(announcementId)
                if (response.code() != 200) {
                    Log.e("Network error", response.message())
                    _uiState.value = UiState.NetworkError("Network error")
                } else {
                    _uiState.value = UiState.Idle
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.NetworkError("Network error")
            }
        }
    }

    fun getUsersAnnouncements() {
        viewModelScope.launch {
            try {
                if (user != null) {
                    _uiState.value = UiState.Loading
                    val response = mainApi.findByAuthor(user!!.id)
                    if (response.code() != 200) {
                        Log.e("Network error", response.message())
                        _uiState.value = UiState.NetworkError("Network error")
                    } else {
                        _usersAnnouncements.value = response.body()
                        _uiState.value = UiState.Idle
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.NetworkError("Network error")
            }
        }
    }

    fun updateAnnouncement(announcementId: Long) {
        if (validateForm()) {
            viewModelScope.launch {
                var uploadedImages: List<Image>? = null
                try {
                    _uiState.value = UiState.Loading

                    val imagesToUpload =
                        allImageSources.filter { it.isLocal() }.map { it.uri }
                    uploadedImages = storageRepository.uploadImages(imagesToUpload)

                    val allImages = mutableListOf<Image>()
                    allImages.addAll(_allImageSources.filter { it.isRemote() }
                        .map { (it as ImageSource.Remote).image })
                    uploadedImages.let { allImages.addAll(it) }

                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    val announcement = Announcement(
                        id = 0L,
                        status = StatusType.PUBLISHED,
                        publishDate = isoFormat.format(Date()),
                        description = description,
                        author = user!!,
                        property = createProperty(allImages), // Передаём полный список Image
                        offer = createOffer()
                    )

                    _uiState.value = UiState.Loading
                    val response = mainApi.updateAnnouncement(announcementId, announcement)
                    handlePublishResponse(response)
                } catch (e: Exception) {
                    _uiState.value =
                        UiState.NetworkError("Received some network errors: " + e.message)
                    if (uploadedImages != null) {
                        for (image in uploadedImages) {
                            storageRepository.deleteImage(image.url)
                        }
                    }
                    e.printStackTrace()
                }
            }
        }
    }

    fun setupUser() {
        viewModelScope.launch {
            currentUserState.userState.collect { state ->
                when (state) {
                    is CurrentUserState.UserState.Loading -> {
                        _uiState.value = UiState.Loading
                    }

                    is CurrentUserState.UserState.Loaded -> {
                        user = state.user // Обновляем user
                        _uiState.value = UiState.Idle
                    }

                    is CurrentUserState.UserState.Error -> {
                        _uiState.value = UiState.NetworkError(state.message)
                    }

                    is CurrentUserState.UserState.Unauthenticated -> {
                        _uiState.value = UiState.NetworkError("Unauthorized")
                    }
                }
            }
        }
    }

    fun loadImagesForAnnouncement(announcementId: Long, photoUrls: List<String>) {
        viewModelScope.launch {
            try {
                // Проверяем, нужно ли загружать изображения
                val currentImages = _loadedImages.value[announcementId]
                if (currentImages == null || currentImages.isEmpty()) {
                    // Загружаем изображения, предполагая, что downloadImages возвращает List<Image>
                    val imageList = storageRepo.downloadImages(photoUrls)
                    _loadedImages.update { currentMap ->
                        currentMap.toMutableMap()
                            .apply { put(announcementId, imageList.map { Image(0, it) }) }
                    }
                    Log.d(
                        "Announcement $announcementId",
                        "Images loaded successfully: ${imageList.size} images"
                    )
                } else {
                    Log.d(
                        "Announcement $announcementId",
                        "Images already loaded: ${currentImages.size} images"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Announcement $announcementId", "Failed to load images: ${e.message}")
                _loadedImages.update { currentMap ->
                    currentMap.toMutableMap().apply { put(announcementId, emptyList()) }
                }
                // Уведомляем UI об ошибке через _uiState
                _uiState.value =
                    UiState.NetworkError("Failed to load images for announcement $announcementId: ${e.message}")
            }
        }
    }

    private fun validateForm(): Boolean {
        val validationMap: MutableMap<String, String> = mutableMapOf()
        validateProperty(validationMap)
        when (selectedPropertyType) {
            PropertyType.House -> validateHouse(validationMap)
            PropertyType.Apartment -> validateApartment(validationMap)
            PropertyType.Room -> validateRoom(validationMap)
            null -> false
        }
        if (validationMap.isNotEmpty()) {
            Log.d("Validate", "Validation error: $validationMap")
            _uiState.value = UiState.ValidationError(validationMap)
            return false
        }
        return true
    }

    private fun validateProperty(validationMap: MutableMap<String, String>): Boolean {
        if (address.isBlank()) {
            validationMap.put("address", "Invalid address")
        }
        if (totalArea.toDoubleOrNull()?.let { it > 0 } != true) {
            validationMap.put("totalArea", "Invalid total area")
        }
        if (price.toDoubleOrNull()?.let { it > 0 } != true) {
            validationMap.put("price", "Invalid price")
        }
        if (floorsNum.toIntOrNull()?.let { it > 0 } != true) {
            validationMap.put("floorsNum", "Invalid amount of floors")
        }
        return validationMap.isEmpty()
    }

    private fun validateHouse(validationMap: MutableMap<String, String>): Boolean {
        if (siteArea.toDoubleOrNull()?.let { it > 0 } != true) {
            validationMap.put("siteArea", "Invalid site area")
        }
        if (selectedEarthStatus == null) {
            validationMap.put("selectedEarthType", "Input earth status")
        }
        if (bedroomsNum == null) {
            validationMap.put("bedroomsNum", "Select amount of bedrooms")
        }
        return validationMap.isEmpty()
    }

    private fun validateApartment(validationMap: MutableMap<String, String>): Boolean {
        if (roomsNum == null) {
            validationMap.put("bedroomsNum", "Select amount of rooms")
        }
        if (livingArea.toDoubleOrNull()?.let { it > 0 } != true) {
            validationMap.put("livingArea", "Invalid living area")
        }
        if (floor.toIntOrNull()?.let { it > 0 && it < floorsNum.toInt() } != true) {
            validationMap.put("floor", "Invalid floor")
        }
        return validationMap.isEmpty()
    }

    private fun validateRoom(validationMap: MutableMap<String, String>): Boolean {
        if (roomsNumInOffer == null) {
            validationMap.put("roomsNumInOffer", "Select amount of rooms in offer")
        }
        if (roomsNumInApartment == null) {
            validationMap.put("roomsNumInApartment", "Select amount of rooms in apartment")
        }
        if (livingArea.toDoubleOrNull()?.let { it > 0 } != true) {
            validationMap.put("livingArea", "Invalid living area")
        }
        if (floor.toIntOrNull()?.let { it > 0 && it < floorsNum.toInt() } != true) {
            validationMap.put("floor", "Invalid floor")
        }
        return validationMap.isEmpty()
    }

    fun publishAnnouncement() {
        Log.d("Announcement", "Publish announcement")
        if (validateForm()) {
            viewModelScope.launch {
                var uploadedImages: List<Image>? = null
                try {
                    _uiState.value = UiState.Loading
                    uploadedImages =
                        storageRepository.uploadImages(_allImageSources.filter { it.isLocal() }
                            .map { it.uri })
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    val announcement = Announcement(
                        id = 0L,
                        status = StatusType.PUBLISHED,
                        publishDate = isoFormat.format(Date()),
                        description = description,
                        author = user!!,
                        property = createProperty(uploadedImages),
                        offer = createOffer()
                    )
                    val response = mainApi.addAnnouncement(announcement)
                    handlePublishResponse(response)
                } catch (e: Exception) {
                    _uiState.value =
                        UiState.NetworkError("Received some network errors: " + e.message)
                    if (uploadedImages != null) {
                        for (image in uploadedImages) {
                            storageRepository.deleteImage(image.url)
                        }
                    }
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handlePublishResponse(response: Response<String>) {
        if (response.isSuccessful) {
            _uiState.value = UiState.Success
        } else {
            _uiState.value = UiState.NetworkError("Received some network errors")
        }
    }

    fun createProperty(images: List<Image>): Property {
        Log.d("Floor", floor)
        return when (selectedPropertyType) {
            PropertyType.Room -> Room(
                id = 0L,
                latitude = 1.0,
                longitude = 1.0,
                address = address,
                area = totalArea.toDouble(),
                bargain = negotiablePrice,
                floors = floorsNum.toInt(),
                photos = images,
                infrastructure = selectedInfrastructure.map { type ->
                    InfrastructureType.getByType(type)!!
                },
                roomsInApartment = roomsNumInApartment!!.toInt(),
                roomsInOffer = roomsNumInOffer!!.toInt(),
                livingArea = livingArea.toDouble(),
                floor = floor.toInt()
            )

            PropertyType.House -> House(
                id = 0L,
                latitude = 1.0,
                longitude = 1.0,
                address = address,
                area = totalArea.toDouble(),
                bargain = negotiablePrice,
                floors = floorsNum.toInt(),
                photos = images,
                infrastructure = selectedInfrastructure.map { type ->
                    InfrastructureType.getByType(type)!!
                },
                earthStatus = selectedEarthStatus!!,
                siteArea = siteArea.toDouble(),
                bedrooms = bedroomsNum!!.toInt(),
            )

            PropertyType.Apartment -> Apartment(
                id = 0L,
                latitude = 1.0,
                longitude = 1.0,
                address = address,
                area = totalArea.toDouble(),
                bargain = negotiablePrice,
                floors = floorsNum.toInt(),
                photos = images,
                infrastructure = selectedInfrastructure.map { type ->
                    InfrastructureType.getByType(type)!!
                },
                rooms = roomsNum!!.toInt(),
                floor = floor.toInt(),
                livingArea = livingArea.toDouble(),
            )

            null -> House(
                id = 0L,
                latitude = 1.0,
                longitude = 1.0,
                address = address,
                area = totalArea.toDouble(),
                bargain = negotiablePrice,
                floors = floorsNum.toInt(),
                photos = images,
                infrastructure = selectedInfrastructure.map { type ->
                    InfrastructureType.getByType(type)!!
                },
                earthStatus = selectedEarthStatus!!,
                siteArea = siteArea.toDouble(),
                bedrooms = bedroomsNum!!.toInt(),
            )
        }
    }

    fun createOffer(): Offer {
        return Offer(
            0L,
            price.toInt()
        )
    }

}