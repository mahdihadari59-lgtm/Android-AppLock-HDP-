package com.example.hormozgansmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hormozgansmart.data.model.BandariPhrase
import com.example.hormozgansmart.data.model.BandariProverb
import com.example.hormozgansmart.data.model.DialectWord
import com.example.hormozgansmart.data.repository.BandariDictionaryRepository
import com.example.hormozgansmart.engine.TranslationDirection
import com.example.hormozgansmart.ui.components.AppSearchBar
import com.example.hormozgansmart.ui.components.FavoriteIconButton
import com.example.hormozgansmart.ui.components.SpeakIconButton
import com.example.hormozgansmart.ui.theme.SaffronSecondary
import com.example.hormozgansmart.ui.theme.SaffronSecondaryDark
import com.example.hormozgansmart.ui.theme.TealPrimary
import com.example.hormozgansmart.viewmodel.MainViewModel

@Composable
fun DialectScreen(viewModel: MainViewModel) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("واژه‌نامه و مترجم", "عبارات رایج", "ضرب‌المثل‌ها", "قواعد دستوری")

    val selectedCategory by viewModel.selectedWordCategory.collectAsState()
    val searchQuery by viewModel.lexiconSearch.collectAsState()
    val translatorInput by viewModel.translatorInput.collectAsState()
    val translationResult by viewModel.translationResult.collectAsState()
    val direction by viewModel.translationDirection.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dialect_screen")
    ) {
        // Sub-Tab selector
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedSubTab) {
            0 -> LexiconAndTranslatorTab(
                viewModel = viewModel,
                selectedCategory = selectedCategory,
                searchQuery = searchQuery,
                translatorInput = translatorInput,
                translationResult = translationResult,
                direction = direction,
                favorites = favorites.map { it.id }.toSet()
            )
            1 -> PhrasesTab(viewModel = viewModel)
            2 -> ProverbsTab(viewModel = viewModel)
            3 -> GrammarTab()
        }
    }
}

@Composable
fun LexiconAndTranslatorTab(
    viewModel: MainViewModel,
    selectedCategory: String,
    searchQuery: String,
    translatorInput: String,
    translationResult: com.example.hormozgansmart.engine.TranslationResult?,
    direction: TranslationDirection,
    favorites: Set<String>
) {
    val filteredWords = remember(selectedCategory, searchQuery) {
        BandariDictionaryRepository.words.filter { word ->
            val matchCategory = selectedCategory == "all" || word.category == selectedCategory
            val matchSearch = searchQuery.isBlank() ||
                    word.persian.contains(searchQuery, ignoreCase = true) ||
                    word.bandari.contains(searchQuery, ignoreCase = true) ||
                    word.phonetic.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Live Two-Way Translator Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("translator_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مترجم هوشمند بندری",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Direction Switcher
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TealPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.clickable {
                                val next = when (direction) {
                                    TranslationDirection.AUTO_DETECT -> TranslationDirection.PERSIAN_TO_BANDARI
                                    TranslationDirection.PERSIAN_TO_BANDARI -> TranslationDirection.BANDARI_TO_PERSIAN
                                    TranslationDirection.BANDARI_TO_PERSIAN -> TranslationDirection.AUTO_DETECT
                                }
                                viewModel.setTranslationDirection(next)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (direction) {
                                        TranslationDirection.AUTO_DETECT -> "تشخیص خودکار"
                                        TranslationDirection.PERSIAN_TO_BANDARI -> "فارسی ➔ بندری"
                                        TranslationDirection.BANDARI_TO_PERSIAN -> "بندری ➔ فارسی"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TealPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "تغییر جهت",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = translatorInput,
                        onValueChange = { viewModel.setTranslatorInput(it) },
                        placeholder = { Text("کلمه یا جمله مورد نظر را تایپ کنید...") },
                        modifier = Modifier.fillMaxWidth().testTag("translator_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        minLines = 2,
                        maxLines = 4
                    )

                    if (translationResult != null && translationResult.translatedText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SaffronSecondary.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ترجمه:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SaffronSecondaryDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                    SpeakIconButton(
                                        onSpeak = { viewModel.speakText(translationResult.translatedText) }
                                    )
                                }
                                Text(
                                    text = translationResult.translatedText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TealPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = translationResult.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search in Dictionary
        item {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setLexiconSearch(it) },
                placeholder = "جستجو در واژگان بندری و فارسی...",
                testTag = "lexicon_search"
            )
        }

        // Categories Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(BandariDictionaryRepository.categories) { (key, label) ->
                    val isSelected = selectedCategory == key
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { viewModel.setWordCategory(key) }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "${filteredWords.size} واژه یافت شد",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Words List
        items(filteredWords, key = { it.id }) { word ->
            val isFav = favorites.contains(word.id)
            WordCard(
                word = word,
                isFavorite = isFav,
                onToggleFavorite = {
                    viewModel.toggleFavorite(
                        id = word.id,
                        itemType = "WORD",
                        title = "${word.persian} ➔ ${word.bandari}",
                        subtitle = "${word.categoryLabel} (${word.dialectRegion})"
                    )
                },
                onSpeak = { viewModel.speakText(word.bandari) }
            )
        }
    }
}

@Composable
fun WordCard(
    word: DialectWord,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSpeak: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("word_card_${word.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = word.persian,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "  ➔  ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = word.bandari,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = TealPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SpeakIconButton(onSpeak = onSpeak)
                    FavoriteIconButton(
                        isFavorite = isFavorite,
                        onToggle = onToggleFavorite
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = word.categoryLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SaffronSecondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "گویش ${word.dialectRegion}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronSecondaryDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (word.phonetic.isNotBlank()) {
                    Text(
                        text = "[ ${word.phonetic} ]",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (word.exampleBandari.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "مثال کاربردی بندری: «${word.exampleBandari}»",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TealPrimary
                        )
                        if (word.examplePersian.isNotBlank()) {
                            Text(
                                text = "معادل: ${word.examplePersian}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PhrasesTab(viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(BandariDictionaryRepository.phrases) { phrase ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = phrase.bandari,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        SpeakIconButton(onSpeak = { viewModel.speakText(phrase.bandari) })
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "معنی: ${phrase.persian}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (phrase.meaning.isNotBlank()) {
                        Text(
                            text = "توضیح: ${phrase.meaning}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProverbsTab(viewModel: MainViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(BandariDictionaryRepository.proverbs) { proverb ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "« ${proverb.proverb} »",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        SpeakIconButton(onSpeak = { viewModel.speakText(proverb.proverb) })
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مفهوم: ${proverb.meaning}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SaffronSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = proverb.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GrammarTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ویژگی‌ها و ساختار دستوری گویش بندری",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "گویش بندری یکی از کهن‌ترین شاخه‌های زبان‌های ایرانی جنوب غربی است که واژگان اصیل پهلوی و نشانه‌های تعامل دریایی با فرهنگ‌های خلیج فارس در آن حفظ شده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(BandariDictionaryRepository.grammarRules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SaffronSecondary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = rule,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
