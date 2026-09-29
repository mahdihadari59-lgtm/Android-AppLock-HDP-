package com.example.hormozgansmart.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hormozgansmart.data.model.PointOfInterest
import com.example.hormozgansmart.data.repository.BandariDictionaryRepository
import com.example.hormozgansmart.ui.components.AppHeader
import com.example.hormozgansmart.ui.components.FavoriteIconButton
import com.example.hormozgansmart.ui.components.SectionHeader
import com.example.hormozgansmart.ui.components.SpeakIconButton
import com.example.hormozgansmart.ui.components.WeatherCard
import com.example.hormozgansmart.ui.theme.CoralRed
import com.example.hormozgansmart.ui.theme.GulfBlueTertiary
import com.example.hormozgansmart.ui.theme.SaffronSecondary
import com.example.hormozgansmart.ui.theme.SaffronSecondaryDark
import com.example.hormozgansmart.ui.theme.SeaGreen
import com.example.hormozgansmart.ui.theme.TealPrimary
import com.example.hormozgansmart.ui.theme.TealPrimaryDark
import com.example.hormozgansmart.viewmodel.AppTab
import com.example.hormozgansmart.viewmodel.MainViewModel

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // App Header
        item {
            AppHeader()
        }

        // Hero Banner: Welcome to Hormozgan Smart
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(TealPrimary, TealPrimaryDark)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "به هرمزگان خوش آمدید",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "سیستم یکپارچه دانش، ترافیک و گویش بندری",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SaffronSecondary.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBoat,
                                contentDescription = null,
                                tint = SaffronSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setTab(AppTab.COPILOT) },
                            modifier = Modifier.weight(1f).testTag("quick_chat_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronSecondary,
                                contentColor = Color(0xFF17242A)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("دستیار هوشمند", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { viewModel.setTab(AppTab.MAP) },
                            modifier = Modifier.weight(1f).testTag("quick_map_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.18f),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نقشه و اماکن", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Emergency Fast Numbers Grid
        item {
            SectionHeader(
                title = "فوریت‌ها و شماره‌های امدادی",
                icon = Icons.Default.Shield
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmergencyButton(
                    title = "پلیس",
                    number = "110",
                    color = GulfBlueTertiary,
                    modifier = Modifier.weight(1f),
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:110"))
                        context.startActivity(intent)
                    }
                )
                EmergencyButton(
                    title = "اورژانس",
                    number = "115",
                    color = CoralRed,
                    modifier = Modifier.weight(1f),
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:115"))
                        context.startActivity(intent)
                    }
                )
                EmergencyButton(
                    title = "نجات دریایی",
                    number = "1550",
                    color = TealPrimary,
                    modifier = Modifier.weight(1f),
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1550"))
                        context.startActivity(intent)
                    }
                )
                EmergencyButton(
                    title = "آتش‌نشانی",
                    number = "125",
                    color = SaffronSecondaryDark,
                    modifier = Modifier.weight(1f),
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:125"))
                        context.startActivity(intent)
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Coastal Weather & Marine Ferry Conditions
        item {
            SectionHeader(
                title = "وضعیت آب‌وهوا و تردد شناورها در خلیج فارس",
                icon = Icons.Default.DirectionsBoat,
                actionText = "به‌روزرسانی"
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.coastalWeather) { weather ->
                    WeatherCard(weather = weather)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick Feature Portals (Lexicon, Traffic, Knowledge)
        item {
            SectionHeader(
                title = "بخش‌های تخصصی سیستم",
                icon = Icons.Default.Explore
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeaturePortalCard(
                    title = "گویش بندری",
                    subtitle = "فرهنگ لغت و مترجم",
                    icon = Icons.Default.Translate,
                    color = TealPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(AppTab.LEXICON) }
                )
                FeaturePortalCard(
                    title = "دانش‌گراف",
                    subtitle = "دانشنامه هرمزگان",
                    icon = Icons.Default.MenuBook,
                    color = SaffronSecondaryDark,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(AppTab.KNOWLEDGE) }
                )
                FeaturePortalCard(
                    title = "دوربین و ترافیک",
                    subtitle = "پایش راه‌های استان",
                    icon = Icons.Default.Videocam,
                    color = GulfBlueTertiary,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(AppTab.KNOWLEDGE) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bandari Word of the Day & Proverb
        item {
            SectionHeader(
                title = "واژه و حکمت روز بندری",
                icon = Icons.Default.Translate,
                actionText = "همه واژه‌ها",
                onActionClick = { viewModel.setTab(AppTab.LEXICON) }
            )
            val featuredProverb = BandariDictionaryRepository.proverbs.first()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SaffronSecondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "ضرب‌المثل اصیل هرمزگان",
                                style = MaterialTheme.typography.labelSmall,
                                color = SaffronSecondaryDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        SpeakIconButton(
                            onSpeak = { viewModel.speakText(featuredProverb.proverb) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "« ${featuredProverb.proverb} »",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "معنی: ${featuredProverb.meaning}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = featuredProverb.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Featured POIs in Bandar Abbas
        item {
            SectionHeader(
                title = "اماکن شاخص بندرعباس",
                icon = Icons.Default.Directions,
                actionText = "مشاهده روی نقشه",
                onActionClick = { viewModel.setTab(AppTab.MAP) }
            )
        }

        items(viewModel.featuredPois) { poi ->
            val isFav = favorites.any { it.id == poi.id }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable {
                        viewModel.selectPoi(poi)
                        viewModel.setTab(AppTab.MAP)
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = poi.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = poi.categoryFa,
                            style = MaterialTheme.typography.labelSmall,
                            color = TealPrimary
                        )
                        Text(
                            text = poi.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FavoriteIconButton(
                            isFavorite = isFav,
                            onToggle = {
                                viewModel.toggleFavorite(
                                    id = poi.id,
                                    itemType = "POI",
                                    title = poi.name,
                                    subtitle = poi.categoryFa
                                )
                            }
                        )
                        IconButton(
                            onClick = {
                                viewModel.calculateRoute(poi)
                                viewModel.setTab(AppTab.MAP)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "مسیریابی",
                                tint = TealPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyButton(
    title: String,
    number: String,
    color: Color,
    modifier: Modifier = Modifier,
    onCall: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onCall() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun FeaturePortalCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun IconButton(onClick: () -> Unit, modifier: Modifier, content: @Composable () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick, modifier = modifier) {
        content()
    }
}
