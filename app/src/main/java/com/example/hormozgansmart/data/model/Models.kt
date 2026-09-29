package com.example.hormozgansmart.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DialectWord(
    val id: String,
    val persian: String,
    val bandari: String,
    val phonetic: String = "",
    val category: String,
    val categoryLabel: String,
    val dialectRegion: String = "بندرعباس", // بندرعباس، قشم، میناب، لنگه، بستک
    val examplePersian: String = "",
    val exampleBandari: String = ""
)

@Serializable
data class BandariPhrase(
    val bandari: String,
    val persian: String,
    val meaning: String = ""
)

@Serializable
data class BandariProverb(
    val proverb: String,
    val meaning: String,
    val explanation: String = ""
)

@Serializable
data class KnowledgeItem(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val categoryFa: String,
    val city: String = "بندرعباس",
    val keywords: List<String> = emptyList(),
    val source: String = "HDP-Atlas"
)

enum class PoiCategory(val labelFa: String, val iconName: String) {
    ALL("همه اماکن", "category"),
    HOSPITAL("درمان و بیمارستان", "local_hospital"),
    TOURISM("گردشگری و تاریخی", "attractions"),
    SHOPPING("مراکز خرید و بازار", "shopping_bag"),
    PIER_TRANSPORT("اسکله‌ها و ترابری", "directions_boat"),
    PARK_BEACH("پارک و ساحل", "park"),
    HOTEL("هتل و اقامتگاه", "hotel"),
    TRAFFIC("دوربین و راهور", "videocam")
}

@Serializable
data class PointOfInterest(
    val id: String,
    val name: String,
    val category: String,
    val categoryFa: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String = "",
    val description: String = "",
    val rating: Double = 4.5,
    val isFeatured: Boolean = false
)

@Serializable
data class TrafficCamera(
    val id: String,
    val name: String,
    val location: String,
    val status: String,
    val speedLimit: Int,
    val isCaution: Boolean = false
)

@Serializable
data class TrafficHotspot(
    val id: String,
    val name: String,
    val riskLevel: String, // بالا، متوسط، هشدار
    val description: String,
    val safetyTips: String
)

@Serializable
data class CoastalWeather(
    val city: String,
    val tempC: Int,
    val humidityPercent: Int, // درصد شرجی
    val windSpeedKm: Int,
    val seaStatus: String, // آرام، نیمه مواج، طوفانی
    val ferryOpen: Boolean, // وضعیت اسکله حقانی به قشم/هرمز
    val conditionText: String
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val bandariNotes: String? = null,
    val isError: Boolean = false
)

data class RoutePlan(
    val destination: PointOfInterest,
    val distanceKm: Double,
    val durationMinutes: Int,
    val travelMode: String, // car, walk, transit
    val steps: List<String>
)
