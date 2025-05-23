package com.example.safetyandcheap.ui.screens.list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.service.PropertyType
import com.example.safetyandcheap.service.dto.EarthStatusType
import com.example.safetyandcheap.service.dto.InfrastructureType
import com.example.safetyandcheap.service.dto.search.AnnouncementSearchDto
import com.example.safetyandcheap.service.dto.search.ApartmentSearchDto
import com.example.safetyandcheap.service.dto.search.HouseSearchDto
import com.example.safetyandcheap.service.dto.search.OfferSearchDto
import com.example.safetyandcheap.service.dto.search.RoomSearchDto
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.ContrastNumberRangeSwitcher
import com.example.safetyandcheap.ui.util.ContrastRangeInputField
import com.example.safetyandcheap.ui.util.FilterText
import com.example.safetyandcheap.ui.util.LowContrastSwitcher
import com.example.safetyandcheap.ui.util.OutlinedContrastButton
import com.example.safetyandcheap.ui.util.SelectableTagsRow
import com.example.safetyandcheap.viewmodel.ListViewModel

@Composable
fun FilterScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel(),
    onApply: () -> Unit = {}
) {
    val filters by viewModel.searchFilters.collectAsState()
    var localFilters by remember { mutableStateOf(AnnouncementSearchDto(
        property = HouseSearchDto()
    )) }

    LaunchedEffect(filters) {
        Log.d("filters", "${filters}")
        if(filters.property != null || filters.offer != null || filters.statuses != null) {
            localFilters = filters
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 40.dp)
            ) {
                OutlinedContrastButton(
                    modifier = Modifier.weight(1f).height(50.dp),
                    onClick = {
                        viewModel.resetFilters()
                        localFilters = viewModel.searchFilters.value
                    }
                ) {
                    Text(
                        text = "Reset",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight(700)
                        )
                    )
                }
                Spacer(Modifier.width(8.dp))
                ContrastButton(
                    modifier = Modifier.weight(1f).height(50.dp),
                    onClick = {
                        viewModel.updateFilters(localFilters)
                        onApply()
                    }
                ) {
                    Text(
                        text = "Apply",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight(700)
                        )
                    )
                }
            }
        },
        modifier = modifier.padding(horizontal = 16.dp)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(22.dp))
            LowContrastSwitcher(
                onOptionSelected = { selected ->
                    localFilters = AnnouncementSearchDto(
                        property = when (PropertyType.getByLocalName(selected)) {
                            PropertyType.House -> HouseSearchDto()
                            PropertyType.Apartment -> ApartmentSearchDto()
                            PropertyType.Room -> RoomSearchDto()
                            else -> localFilters.property
                        },
                        offer = localFilters.offer
                    )
                },
                options = PropertyType.entries.map { it.localName },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(22.dp))
            FilterText("Floors")
            Spacer(Modifier.height(10.dp))
            ContrastRangeInputField(
                modifier = Modifier.fillMaxWidth(),
                min = localFilters.property?.minFloors,
                max = localFilters.property?.maxFloors,
                onMinChange = { localFilters = localFilters.copyWithProperty(minFloors = it) },
                onMaxChange = { localFilters = localFilters.copyWithProperty(maxFloors = it) }
            )
            Spacer(Modifier.height(20.dp))
            FilterText("Price, $")
            Spacer(Modifier.height(10.dp))
            ContrastRangeInputField(
                modifier = Modifier.fillMaxWidth(),
                min = localFilters.offer?.minPrice,
                max = localFilters.offer?.maxPrice,
                onMinChange = { localFilters = localFilters.copyWithOffer(minPrice = it) },
                onMaxChange = { localFilters = localFilters.copyWithOffer(maxPrice = it) }
            )
            Spacer(Modifier.height(20.dp))
            FilterText("Total area, m²")
            Spacer(Modifier.height(10.dp))
            ContrastRangeInputField(
                modifier = Modifier.fillMaxWidth(),
                min = localFilters.property?.minArea?.toInt(),
                max = localFilters.property?.maxArea?.toInt(),
                onMinChange = { localFilters = localFilters.copyWithProperty(minArea = it?.toDouble()) },
                onMaxChange = { localFilters = localFilters.copyWithProperty(maxArea = it?.toDouble()) }
            )
            Spacer(Modifier.height(20.dp))
            FilterText("Infrastructure")
            Spacer(Modifier.height(10.dp))
            SelectableTagsRow(
                tags = InfrastructureType.entries.map { it.type }.toSet(),
                selectedTags = localFilters.property?.infrastructure?.map { it.type }?.toSet() ?: emptySet(),
                onSelectedChange = {
                    localFilters = localFilters.copyWithProperty(
                        infrastructure = it.mapNotNull { type ->
                            InfrastructureType.entries.find { it.type == type }
                        }
                    )
                }
            )
            Spacer(Modifier.height(24.dp))

            when (val property = localFilters.property) {
                is HouseSearchDto -> {
                    HouseFilterPart(
                        onNumberOfBedroomsChange = { min, max ->
                            localFilters = localFilters.copyWithProperty(
                                minBedrooms = min,
                                maxBedrooms = max
                            )
                        },
                        numberOfBedroomsMin = property.minBedrooms,
                        numberOfBedroomsMax = property.maxBedrooms,
                        siteAreaMin = property.minSiteArea?.toInt(),
                        siteAreaMax = property.maxSiteArea?.toInt(),
                        onSiteAreaMinChange = {
                            localFilters = localFilters.copyWithProperty(minSiteArea = it?.toDouble())
                        },
                        onSiteAreaMaxChange = {
                            localFilters = localFilters.copyWithProperty(maxSiteArea = it?.toDouble())
                        },
                        onSelectedEarthStatus = {
                            localFilters = localFilters.copyWithProperty(earthStatuses = it.toList())
                        },
                        selectedEarthStatuses = property.earthStatuses?.toSet() ?: emptySet()
                    )
                }
                is ApartmentSearchDto -> {
                    ApartmentFilterPart(
                        onNumberOfRoomsChange = { min, max ->
                            localFilters = localFilters.copyWithProperty(
                                minRooms = min,
                                maxRooms = max
                            )
                        },
                        numberOfRoomsMin = property.minRooms,
                        numberOfRoomsMax = property.maxRooms,
                        livingAreaMin = property.minLivingArea?.toInt(),
                        livingAreaMax = property.maxLivingArea?.toInt(),
                        onLivingAreaMinChange = {
                            localFilters = localFilters.copyWithProperty(minLivingArea = it?.toDouble())
                        },
                        onLivingAreaMaxChange = {
                            localFilters = localFilters.copyWithProperty(maxLivingArea = it?.toDouble())
                        },
                        floorMin = property.minFloor,
                        floorMax = property.maxFloor,
                        onFloorMinChange = {
                            localFilters = localFilters.copyWithProperty(minFloor = it)
                        },
                        onFloorMaxChange = {
                            localFilters = localFilters.copyWithProperty(maxFloor = it)
                        }
                    )
                }
                is RoomSearchDto -> {
                    RoomFilterPart(
                        onNumberOfRoomsInApartmentChange = { min, max ->
                            localFilters = localFilters.copyWithProperty(
                                minRoomsInApartment = min,
                                maxRoomsInApartment = max
                            )
                        },
                        numberOfRoomsInApartmentMin = property.minRoomsInApartment,
                        numberOfRoomsInApartmentMax = property.maxRoomsInApartment,
                        livingAreaMin = property.minLivingArea?.toInt(),
                        livingAreaMax = property.maxLivingArea?.toInt(),
                        onLivingAreaMinChange = {
                            localFilters = localFilters.copyWithProperty(minLivingArea = it?.toDouble())
                        },
                        onLivingAreaMaxChange = {
                            localFilters = localFilters.copyWithProperty(maxLivingArea = it?.toDouble())
                        },
                        floorMin = property.minFloor,
                        floorMax = property.maxFloor,
                        onFloorMinChange = {
                            localFilters = localFilters.copyWithProperty(minFloor = it)
                        },
                        onFloorMaxChange = {
                            localFilters = localFilters.copyWithProperty(maxFloor = it)
                        },
                        onNumberOfRoomsInOfferChange = { min, max ->
                            localFilters = localFilters.copyWithProperty(
                                minRoomsInOffer = min,
                                maxRoomsInOffer = max
                            )
                        },
                        numberOfRoomsInOfferMin = property.minRoomsInOffer,
                        numberOfRoomsInOfferMax = property.maxRoomsInOffer
                    )
                }
                else -> {
                    Text("Select a property type")
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

private fun AnnouncementSearchDto.copyWithProperty(
    minFloors: Int? = property?.minFloors,
    maxFloors: Int? = property?.maxFloors,
    minArea: Double? = property?.minArea,
    maxArea: Double? = property?.maxArea,
    infrastructure: List<InfrastructureType>? = property?.infrastructure,
    minBedrooms: Int? = (property as? HouseSearchDto)?.minBedrooms,
    maxBedrooms: Int? = (property as? HouseSearchDto)?.maxBedrooms,
    minSiteArea: Double? = (property as? HouseSearchDto)?.minSiteArea,
    maxSiteArea: Double? = (property as? HouseSearchDto)?.maxSiteArea,
    earthStatuses: List<String>? = (property as? HouseSearchDto)?.earthStatuses,
    minRooms: Int? = (property as? ApartmentSearchDto)?.minRooms,
    maxRooms: Int? = (property as? ApartmentSearchDto)?.maxRooms,
    minLivingArea: Double? = (property as? ApartmentSearchDto)?.minLivingArea ?: (property as? RoomSearchDto)?.minLivingArea,
    maxLivingArea: Double? = (property as? ApartmentSearchDto)?.maxLivingArea ?: (property as? RoomSearchDto)?.maxLivingArea,
    minFloor: Int? = (property as? ApartmentSearchDto)?.minFloor ?: (property as? RoomSearchDto)?.minFloor,
    maxFloor: Int? = (property as? ApartmentSearchDto)?.maxFloor ?: (property as? RoomSearchDto)?.maxFloor,
    minRoomsInApartment: Int? = (property as? RoomSearchDto)?.minRoomsInApartment,
    maxRoomsInApartment: Int? = (property as? RoomSearchDto)?.maxRoomsInApartment,
    minRoomsInOffer: Int? = (property as? RoomSearchDto)?.minRoomsInOffer,
    maxRoomsInOffer: Int? = (property as? RoomSearchDto)?.maxRoomsInOffer
): AnnouncementSearchDto {
    return copy(
        property = when (property) {
            is HouseSearchDto -> property.copy(
                minFloors = minFloors,
                maxFloors = maxFloors,
                minArea = minArea,
                maxArea = maxArea,
                infrastructure = infrastructure,
                minBedrooms = minBedrooms,
                maxBedrooms = maxBedrooms,
                minSiteArea = minSiteArea,
                maxSiteArea = maxSiteArea,
                earthStatuses = earthStatuses
            )
            is ApartmentSearchDto -> property.copy(
                minFloors = minFloors,
                maxFloors = maxFloors,
                minArea = minArea,
                maxArea = maxArea,
                infrastructure = infrastructure,
                minRooms = minRooms,
                maxRooms = maxRooms,
                minLivingArea = minLivingArea,
                maxLivingArea = maxLivingArea,
                minFloor = minFloor,
                maxFloor = maxFloor
            )
            is RoomSearchDto -> property.copy(
                minFloors = minFloors,
                maxFloors = maxFloors,
                minArea = minArea,
                maxArea = maxArea,
                infrastructure = infrastructure,
                minRoomsInApartment = minRoomsInApartment,
                maxRoomsInApartment = maxRoomsInApartment,
                minRoomsInOffer = minRoomsInOffer,
                maxRoomsInOffer = maxRoomsInOffer,
                minLivingArea = minLivingArea,
                maxLivingArea = maxLivingArea,
                minFloor = minFloor,
                maxFloor = maxFloor
            )
            else -> property
        },
        offer = offer
    )
}

private fun AnnouncementSearchDto.copyWithOffer(
    minPrice: Int? = offer?.minPrice,
    maxPrice: Int? = offer?.maxPrice
): AnnouncementSearchDto {
    return copy(offer = offer?.copy(minPrice = minPrice, maxPrice = maxPrice) ?: OfferSearchDto(minPrice, maxPrice))
}

@Composable
fun HouseFilterPart(
    onNumberOfBedroomsChange: (Int?, Int?) -> Unit,
    numberOfBedroomsMin: Int?,
    numberOfBedroomsMax: Int?,
    siteAreaMin: Int?,
    siteAreaMax: Int?,
    onSiteAreaMinChange: (Int?) -> Unit,
    onSiteAreaMaxChange: (Int?) -> Unit,
    selectedEarthStatuses: Set<String>,
    onSelectedEarthStatus: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = PropertyType.House.localName,
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.tertiary
            )
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Bedrooms")
        Spacer(Modifier.height(10.dp))
        ContrastNumberRangeSwitcher(
            onOptionSelected = onNumberOfBedroomsChange,
            borders = 1 to 5,
            selectedMin = numberOfBedroomsMin,
            selectedMax = numberOfBedroomsMax,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Site area, m²")
        Spacer(Modifier.height(10.dp))
        ContrastRangeInputField(
            modifier = Modifier.fillMaxWidth(),
            min = siteAreaMin,
            max = siteAreaMax,
            onMinChange = onSiteAreaMinChange,
            onMaxChange = onSiteAreaMaxChange
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Earth status")
        Spacer(Modifier.height(10.dp))
        SelectableTagsRow(
            tags = EarthStatusType.entries.map { it.statusName }.toSet(),
            selectedTags = selectedEarthStatuses,
            onSelectedChange = onSelectedEarthStatus,
        )
    }
}

@Composable
fun ApartmentFilterPart(
    onNumberOfRoomsChange: (Int?, Int?) -> Unit,
    numberOfRoomsMin: Int?,
    numberOfRoomsMax: Int?,
    livingAreaMin: Int?,
    livingAreaMax: Int?,
    onLivingAreaMinChange: (Int?) -> Unit,
    onLivingAreaMaxChange: (Int?) -> Unit,
    floorMin: Int?,
    floorMax: Int?,
    onFloorMinChange: (Int?) -> Unit,
    onFloorMaxChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = PropertyType.Apartment.localName,
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.tertiary
            )
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Rooms")
        Spacer(Modifier.height(10.dp))
        ContrastNumberRangeSwitcher(
            onOptionSelected = onNumberOfRoomsChange,
            borders = 1 to 4,
            selectedMin = numberOfRoomsMin,
            selectedMax = numberOfRoomsMax,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Living area, m²")
        Spacer(Modifier.height(10.dp))
        ContrastRangeInputField(
            modifier = Modifier.fillMaxWidth(),
            min = livingAreaMin,
            max = livingAreaMax,
            onMinChange = onLivingAreaMinChange,
            onMaxChange = onLivingAreaMaxChange
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Floor")
        Spacer(Modifier.height(10.dp))
        ContrastRangeInputField(
            modifier = Modifier.fillMaxWidth(),
            min = floorMin,
            max = floorMax,
            onMinChange = onFloorMinChange,
            onMaxChange = onFloorMaxChange
        )
    }
}

@Composable
fun RoomFilterPart(
    onNumberOfRoomsInApartmentChange: (Int?, Int?) -> Unit,
    numberOfRoomsInApartmentMin: Int?,
    numberOfRoomsInApartmentMax: Int?,
    livingAreaMin: Int?,
    livingAreaMax: Int?,
    onLivingAreaMinChange: (Int?) -> Unit,
    onLivingAreaMaxChange: (Int?) -> Unit,
    onNumberOfRoomsInOfferChange: (Int?, Int?) -> Unit,
    numberOfRoomsInOfferMin: Int?,
    numberOfRoomsInOfferMax: Int?,
    floorMin: Int?,
    floorMax: Int?,
    onFloorMinChange: (Int?) -> Unit,
    onFloorMaxChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = PropertyType.Room.localName,
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.tertiary
            )
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Rooms in apartment")
        Spacer(Modifier.height(10.dp))
        ContrastNumberRangeSwitcher(
            onOptionSelected = onNumberOfRoomsInApartmentChange,
            borders = 1 to 4,
            selectedMin = numberOfRoomsInApartmentMin,
            selectedMax = numberOfRoomsInApartmentMax,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Rooms in offer")
        Spacer(Modifier.height(10.dp))
        ContrastNumberRangeSwitcher(
            onOptionSelected = onNumberOfRoomsInOfferChange,
            borders = 1 to 4,
            selectedMin = numberOfRoomsInOfferMin,
            selectedMax = numberOfRoomsInOfferMax,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Living area, m²")
        Spacer(Modifier.height(10.dp))
        ContrastRangeInputField(
            modifier = Modifier.fillMaxWidth(),
            min = livingAreaMin,
            max = livingAreaMax,
            onMinChange = onLivingAreaMinChange,
            onMaxChange = onLivingAreaMaxChange
        )
        Spacer(Modifier.height(20.dp))
        FilterText("Floor")
        Spacer(Modifier.height(10.dp))
        ContrastRangeInputField(
            modifier = Modifier.fillMaxWidth(),
            min = floorMin,
            max = floorMax,
            onMinChange = onFloorMinChange,
            onMaxChange = onFloorMaxChange
        )
    }
}