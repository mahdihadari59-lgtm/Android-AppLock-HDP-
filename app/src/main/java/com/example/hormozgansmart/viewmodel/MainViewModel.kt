package com.example.hormozgansmart.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hormozgansmart.data.local.CustomNoteEntity
import com.example.hormozgansmart.data.local.FavoriteEntity
import com.example.hormozgansmart.data.local.HormozganDatabase
import com.example.hormozgansmart.data.model.ChatMessage
import com.example.hormozgansmart.data.model.CoastalWeather
import com.example.hormozgansmart.data.model.DialectWord
import com.example.hormozgansmart.data.model.KnowledgeItem
import com.example.hormozgansmart.data.model.MessageRole
import com.example.hormozgansmart.data.model.PoiCategory
import com.example.hormozgansmart.data.model.PointOfInterest
import com.example.hormozgansmart.data.model.RoutePlan
import com.example.hormozgansmart.data.model.TrafficCamera
import com.example.hormozgansmart.data.model.TrafficHotspot
import com.example.hormozgansmart.data.repository.BandariDictionaryRepository
import com.example.hormozgansmart.data.repository.KnowledgeRepository
import com.example.hormozgansmart.data.repository.PoiRepository
import com.example.hormozgansmart.data.repository.WeatherTrafficRepository
import com.example.hormozgansmart.engine.BandariDialectEngine
import com.example.hormozgansmart.engine.GeminiCopilotService
import com.example.hormozgansmart.engine.TranslationDirection
import com.example.hormozgansmart.engine.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppTab(val titleFa: String) {
    DASHBOARD("داشبورد"),
    LEXICON("فرهنگ لغت"),
    MAP("نقشه و اماکن"),
    COPILOT("دستیار هوشمند"),
    KNOWLEDGE("دانش‌گراف"),
    BOOKMARKS("نشان‌شده‌ها")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HormozganDatabase.getDatabase(application)
    private val dao = db.dao()
    private val copilotService = GeminiCopilotService()

    // TTS
    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    // Navigation
    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Dashboard Data
    val coastalWeather: List<CoastalWeather> = WeatherTrafficRepository.coastalStations
    val featuredPois: List<PointOfInterest> = PoiRepository.pois.filter { it.isFeatured }
    val featuredWords: List<DialectWord> = BandariDictionaryRepository.words.take(6)

    // Lexicon Tab State
    private val _selectedWordCategory = MutableStateFlow("all")
    val selectedWordCategory: StateFlow<String> = _selectedWordCategory.asStateFlow()

    private val _lexiconSearch = MutableStateFlow("")
    val lexiconSearch: StateFlow<String> = _lexiconSearch.asStateFlow()

    private val _translatorInput = MutableStateFlow("")
    val translatorInput: StateFlow<String> = _translatorInput.asStateFlow()

    private val _translationResult = MutableStateFlow<TranslationResult?>(null)
    val translationResult: StateFlow<TranslationResult?> = _translationResult.asStateFlow()

    private val _translationDirection = MutableStateFlow(TranslationDirection.AUTO_DETECT)
    val translationDirection: StateFlow<TranslationDirection> = _translationDirection.asStateFlow()

    fun setWordCategory(category: String) {
        _selectedWordCategory.value = category
    }

    fun setLexiconSearch(query: String) {
        _lexiconSearch.value = query
    }

    fun setTranslatorInput(text: String) {
        _translatorInput.value = text
        if (text.isNotBlank()) {
            _translationResult.value = BandariDialectEngine.translate(text, _translationDirection.value)
        } else {
            _translationResult.value = null
        }
    }

    fun setTranslationDirection(direction: TranslationDirection) {
        _translationDirection.value = direction
        if (_translatorInput.value.isNotBlank()) {
            _translationResult.value = BandariDialectEngine.translate(_translatorInput.value, direction)
        }
    }

    // Map & POI State
    private val _selectedPoiCategory = MutableStateFlow(PoiCategory.ALL)
    val selectedPoiCategory: StateFlow<PoiCategory> = _selectedPoiCategory.asStateFlow()

    private val _mapSearchQuery = MutableStateFlow("")
    val mapSearchQuery: StateFlow<String> = _mapSearchQuery.asStateFlow()

    private val _selectedPoi = MutableStateFlow<PointOfInterest?>(null)
    val selectedPoi: StateFlow<PointOfInterest?> = _selectedPoi.asStateFlow()

    private val _activeRoute = MutableStateFlow<RoutePlan?>(null)
    val activeRoute: StateFlow<RoutePlan?> = _activeRoute.asStateFlow()

    fun setPoiCategory(category: PoiCategory) {
        _selectedPoiCategory.value = category
    }

    fun setMapSearchQuery(query: String) {
        _mapSearchQuery.value = query
    }

    fun selectPoi(poi: PointOfInterest?) {
        _selectedPoi.value = poi
    }

    fun calculateRoute(poi: PointOfInterest, travelMode: String = "car") {
        val baseDistance = when (poi.category) {
            "hospital" -> 3.2
            "pier" -> 4.5
            "transport" -> 6.8
            "tourism" -> 2.1
            "shopping" -> 1.8
            else -> 3.0
        }
        val duration = when (travelMode) {
            "car" -> (baseDistance * 2.5).toInt() + 4
            "walk" -> (baseDistance * 14).toInt()
            else -> (baseDistance * 4.0).toInt() + 8
        }
        val steps = listOf(
            "حرکت از موقعیت فعلی در بندرعباس به سمت بلوار اصلی",
            "طی مسافت ۱.۲ کیلومتر در امتداد بلوار به سمت مقصد",
            "ورود به مسیر اختصاصی ${poi.name}",
            "رسیدن به مقصد در نشانی: ${poi.address}"
        )
        _activeRoute.value = RoutePlan(
            destination = poi,
            distanceKm = baseDistance,
            durationMinutes = duration,
            travelMode = travelMode,
            steps = steps
        )
    }

    fun clearRoute() {
        _activeRoute.value = null
    }

    // Chat / Copilot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                role = MessageRole.ASSISTANT,
                text = "سلام و درود! من «دستیار هوشمند هرمزگان» هستم. می‌توانید درباره جاذبه‌های گردشگری بندرعباس، اسکله حقانی، فرهنگ و گویش بندری، اماکن درمانی یا ترافیک از من سوال بپرسید."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _useBandariDialect = MutableStateFlow(false)
    val useBandariDialect: StateFlow<Boolean> = _useBandariDialect.asStateFlow()

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    fun toggleBandariDialect() {
        _useBandariDialect.value = !_useBandariDialect.value
    }

    fun sendChatMessage(text: String? = null) {
        val query = text ?: _chatInput.value.trim()
        if (query.isBlank()) return

        val userMsg = ChatMessage(role = MessageRole.USER, text = query)
        _chatMessages.value = _chatMessages.value + userMsg
        if (text == null) _chatInput.value = ""
        _isChatLoading.value = true

        viewModelScope.launch {
            try {
                val response = copilotService.getResponse(query, _useBandariDialect.value)
                val assistantMsg = ChatMessage(role = MessageRole.ASSISTANT, text = response)
                _chatMessages.value = _chatMessages.value + assistantMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    role = MessageRole.ASSISTANT,
                    text = "متاسفانه در برقراری ارتباط خطایی رخ داد. لطفاً مجدداً امتحان نمایید.",
                    isError = true
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                role = MessageRole.ASSISTANT,
                text = "گفتگو بازنشانی شد. چطور می‌توانم در مورد بندرعباس و هرمزگان کمکتان کنم؟"
            )
        )
    }

    // Knowledge Base State
    private val _knowledgeSearch = MutableStateFlow("")
    val knowledgeSearch: StateFlow<String> = _knowledgeSearch.asStateFlow()

    private val _knowledgeCategory = MutableStateFlow("all")
    val knowledgeCategory: StateFlow<String> = _knowledgeCategory.asStateFlow()

    fun setKnowledgeSearch(query: String) {
        _knowledgeSearch.value = query
    }

    fun setKnowledgeCategory(cat: String) {
        _knowledgeCategory.value = cat
    }

    // Traffic State
    val trafficCameras: List<TrafficCamera> = PoiRepository.cameras
    val trafficHotspots: List<TrafficHotspot> = PoiRepository.hotspots

    // Room DB Favorites & Notes
    val favorites: StateFlow<List<FavoriteEntity>> = dao.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customNotes: StateFlow<List<CustomNoteEntity>> = dao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(id: String, itemType: String, title: String, subtitle: String, extraData: String = "") {
        viewModelScope.launch {
            val exists = dao.isFavorite(id)
            if (exists) {
                dao.deleteFavoriteById(id)
            } else {
                dao.insertFavorite(
                    FavoriteEntity(
                        id = id,
                        itemType = itemType,
                        title = title,
                        subtitle = subtitle,
                        extraData = extraData
                    )
                )
            }
        }
    }

    fun isItemFavorite(id: String): Boolean {
        return favorites.value.any { it.id == id }
    }

    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            dao.insertNote(
                CustomNoteEntity(
                    title = title,
                    content = content,
                    category = category
                )
            )
        }
    }

    fun deleteNote(note: CustomNoteEntity) {
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }

    // Text To Speech
    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("fa")
                _isTtsReady.value = true
            }
        }
    }

    fun speakText(text: String) {
        if (_isTtsReady.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "HormozganTts")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
