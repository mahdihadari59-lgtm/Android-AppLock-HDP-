package com.example.hormozgansmart.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hormozgansmart.data.model.PoiCategory
import com.example.hormozgansmart.data.model.PointOfInterest
import com.example.hormozgansmart.data.repository.PoiRepository
import com.example.hormozgansmart.ui.components.AppSearchBar
import com.example.hormozgansmart.ui.components.FavoriteIconButton
import com.example.hormozgansmart.ui.theme.CoralRed
import com.example.hormozgansmart.ui.theme.GulfBlueTertiary
import com.example.hormozgansmart.ui.theme.SaffronSecondary
import com.example.hormozgansmart.ui.theme.SaffronSecondaryDark
import com.example.hormozgansmart.ui.theme.SeaGreen
import com.example.hormozgansmart.ui.theme.TealPrimary
import com.example.hormozgansmart.viewmodel.MainViewModel

@Composable
fun MapScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val selectedCategory by viewModel.selectedPoiCategory.collectAsState()
    val searchQuery by viewModel.mapSearchQuery.collectAsState()
    val selectedPoi by viewModel.selectedPoi.collectAsState()
    val activeRoute by viewModel.activeRoute.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var showTrafficLayer by remember { mutableStateOf(false) }

    // Filter POIs
    val filteredPois = remember(selectedCategory, searchQuery) {
        PoiRepository.pois.filter { poi ->
            val matchCat = when (selectedCategory) {
                PoiCategory.ALL -> true
                PoiCategory.HOSPITAL -> poi.category == "hospital"
                PoiCategory.TOURISM -> poi.category == "tourism"
                PoiCategory.SHOPPING -> poi.category == "shopping"
                PoiCategory.PIER_TRANSPORT -> poi.category == "pier" || poi.category == "transport"
                PoiCategory.PARK_BEACH -> poi.category == "park"
                PoiCategory.HOTEL -> poi.category == "hotel"
                PoiCategory.TRAFFIC -> poi.category == "traffic"
            }
            val matchSearch = searchQuery.isBlank() ||
                    poi.name.contains(searchQuery, ignoreCase = true) ||
                    poi.address.contains(searchQuery, ignoreCase = true) ||
                    poi.categoryFa.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("map_screen")
    ) {
        // Map Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("map_canvas")
                .pointerInput(filteredPois) {
                    detectTapGestures { offset ->
                        // Detect tap on POI pins
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val minLat = 27.05
                        val maxLat = 27.42
                        val minLon = 56.15
                        val maxLon = 56.40

                        val tapped = filteredPois.find { poi ->
                            val x = ((poi.longitude - minLon) / (maxLon - minLon) * w).toFloat()
                            val y = ((maxLat - poi.latitude) / (maxLat - minLat) * h).toFloat()
                            val distance = Math.hypot((offset.x - x).toDouble(), (offset.y - y).toDouble())
                            distance < 45
                        }
                        if (tapped != null) {
                            viewModel.selectPoi(tapped)
                        }
                    }
                }
        ) {
            drawHormozganMap(
                pois = filteredPois,
                selectedPoi = selectedPoi,
                showTraffic = showTrafficLayer,
                hasRoute = activeRoute != null
            )
        }

        // Top Controls: Search Bar & Category Filter Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.setMapSearchQuery(it) },
                placeholder = "جستجوی بیمارستان، اسکله، معبد هندوها، بازار...",
                testTag = "map_search"
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(PoiCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { viewModel.setPoiCategory(cat) }
                    ) {
                        Text(
                            text = cat.labelFa,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Floating Control Buttons (Traffic toggle, Center location)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Traffic Layer Toggle
            Surface(
                shape = CircleShape,
                color = if (showTrafficLayer) SaffronSecondary else MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(44.dp)
                    .clickable { showTrafficLayer = !showTrafficLayer }
                    .testTag("traffic_layer_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Traffic,
                        contentDescription = "لایه ترافیک و دوربین‌ها",
                        tint = if (showTrafficLayer) Color(0xFF17242A) else TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Locate User
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        viewModel.selectPoi(null)
                    }
                    .testTag("my_location_btn")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "موقعیت من در بندرعباس",
                        tint = TealPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bottom Sheets: Selected POI Details OR Route Guide
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp)
        ) {
            if (activeRoute != null) {
                // Route Plan Card
                RouteGuideCard(
                    route = activeRoute!!,
                    onClose = { viewModel.clearRoute() },
                    onModeSelect = { mode ->
                        viewModel.calculateRoute(activeRoute!!.destination, mode)
                    }
                )
            } else if (selectedPoi != null) {
                // POI Details Card
                val isFav = favorites.any { it.id == selectedPoi!!.id }
                PoiDetailCard(
                    poi = selectedPoi!!,
                    isFavorite = isFav,
                    onToggleFavorite = {
                        viewModel.toggleFavorite(
                            id = selectedPoi!!.id,
                            itemType = "POI",
                            title = selectedPoi!!.name,
                            subtitle = selectedPoi!!.categoryFa
                        )
                    },
                    onRoute = {
                        viewModel.calculateRoute(selectedPoi!!)
                    },
                    onCall = {
                        if (selectedPoi!!.phone.isNotBlank() && selectedPoi!!.phone != "-") {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${selectedPoi!!.phone}"))
                            context.startActivity(intent)
                        }
                    },
                    onClose = { viewModel.selectPoi(null) }
                )
            }
        }
    }
}

private fun DrawScope.drawHormozganMap(
    pois: List<PointOfInterest>,
    selectedPoi: PointOfInterest?,
    showTraffic: Boolean,
    hasRoute: Boolean
) {
    val w = size.width
    val h = size.height

    // Background: Warm Sand/Land Color
    drawRect(color = Color(0xFFF7F3EB))

    // Water: Persian Gulf Coastline at the bottom
    val seaPath = Path().apply {
        moveTo(0f, h * 0.62f)
        cubicTo(
            w * 0.25f, h * 0.60f,
            w * 0.60f, h * 0.66f,
            w, h * 0.58f
        )
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = seaPath, color = Color(0xFFD4E8EE))

    // Coastal Beach Sand Line
    drawPath(
        path = seaPath,
        color = Color(0xFFE8DECA),
        style = Stroke(width = 8f)
    )

    // Qeshm Island (Representation in Gulf)
    drawOval(
        color = Color(0xFFEDE5D8),
        topLeft = Offset(w * 0.15f, h * 0.78f),
        size = androidx.compose.ui.geometry.Size(w * 0.40f, h * 0.12f)
    )

    // Hormuz Island (Reddish tint)
    drawCircle(
        color = Color(0xFFE5CEBD),
        radius = w * 0.08f,
        center = Offset(w * 0.80f, h * 0.74f)
    )

    // Roads & Highways
    // 1. Coastal Boulevard
    drawLine(
        color = Color(0xFFCBD5E1),
        start = Offset(0f, h * 0.59f),
        end = Offset(w, h * 0.55f),
        strokeWidth = 6f
    )
    // 2. Imam Khomeini Blvd (Central)
    drawLine(
        color = Color(0xFF94A3B8),
        start = Offset(w * 0.10f, h * 0.50f),
        end = Offset(w * 0.90f, h * 0.46f),
        strokeWidth = 5f
    )
    // 3. Northbound Highway to Sirjan / Geno
    drawLine(
        color = Color(0xFF94A3B8),
        start = Offset(w * 0.50f, h * 0.48f),
        end = Offset(w * 0.52f, 0f),
        strokeWidth = 6f
    )
    // 4. West Highway to Shahid Rajaei Port
    drawLine(
        color = Color(0xFFCBD5E1),
        start = Offset(w * 0.25f, h * 0.52f),
        end = Offset(0f, h * 0.45f),
        strokeWidth = 5f
    )

    // Draw Simulated Route Line if active
    if (hasRoute && selectedPoi != null) {
        val minLat = 27.05
        val maxLat = 27.42
        val minLon = 56.15
        val maxLon = 56.40

        val destX = ((selectedPoi.longitude - minLon) / (maxLon - minLon) * w).toFloat()
        val destY = ((maxLat - selectedPoi.latitude) / (maxLat - minLat) * h).toFloat()
        val userX = w * 0.45f
        val userY = h * 0.52f

        val routePath = Path().apply {
            moveTo(userX, userY)
            quadraticTo((userX + destX) / 2f, userY - 30f, destX, destY)
        }
        drawPath(
            path = routePath,
            color = Color(0xFF0284C7),
            style = Stroke(width = 8f)
        )
    }

    // User Location Marker
    val userCenter = Offset(w * 0.45f, h * 0.52f)
    drawCircle(color = Color(0xFF0284C7).copy(alpha = 0.25f), radius = 24f, center = userCenter)
    drawCircle(color = Color.White, radius = 10f, center = userCenter)
    drawCircle(color = Color(0xFF0284C7), radius = 7f, center = userCenter)

    // Draw POI Markers
    val minLat = 27.05
    val maxLat = 27.42
    val minLon = 56.15
    val maxLon = 56.40

    pois.forEach { poi ->
        val x = ((poi.longitude - minLon) / (maxLon - minLon) * w).toFloat()
        val y = ((maxLat - poi.latitude) / (maxLat - minLat) * h).toFloat()
        val isSelected = selectedPoi?.id == poi.id

        val markerColor = when (poi.category) {
            "hospital" -> CoralRed
            "pier" -> TealPrimary
            "transport" -> GulfBlueTertiary
            "tourism" -> SaffronSecondaryDark
            "shopping" -> Color(0xFF8B5CF6)
            "hotel" -> Color(0xFF0D9488)
            "park" -> SeaGreen
            else -> TealPrimary
        }

        if (isSelected) {
            drawCircle(color = markerColor.copy(alpha = 0.35f), radius = 26f, center = Offset(x, y))
        }

        drawCircle(color = Color.White, radius = if (isSelected) 14f else 10f, center = Offset(x, y))
        drawCircle(color = markerColor, radius = if (isSelected) 10f else 7f, center = Offset(x, y))
    }
}

@Composable
fun PoiDetailCard(
    poi: PointOfInterest,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRoute: () -> Unit,
    onCall: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("poi_detail_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = poi.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = poi.categoryFa,
                        style = MaterialTheme.typography.labelSmall,
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FavoriteIconButton(isFavorite = isFavorite, onToggle = onToggleFavorite)
                    IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "بستن",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = poi.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📍 ${poi.address}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRoute,
                    modifier = Modifier.weight(1f).testTag("start_route_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مسیریابی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (poi.phone.isNotBlank() && poi.phone != "-") {
                    OutlinedButton(
                        onClick = onCall,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تماس", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RouteGuideCard(
    route: com.example.hormozgansmart.data.model.RoutePlan,
    onClose: () -> Unit,
    onModeSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("route_guide_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مسیر به سمت ${route.destination.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "فاصله: ${route.distanceKm} کیلومتر • زمان تقریبی: ${route.durationMinutes} دقیقه",
                        style = MaterialTheme.typography.labelSmall,
                        color = GulfBlueTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "پایان مسیر")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Travel Modes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TravelModeChip(
                    label = "خودرو",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = route.travelMode == "car",
                    onClick = { onModeSelect("car") },
                    modifier = Modifier.weight(1f)
                )
                TravelModeChip(
                    label = "پیاده",
                    icon = Icons.Default.DirectionsWalk,
                    isSelected = route.travelMode == "walk",
                    onClick = { onModeSelect("walk") },
                    modifier = Modifier.weight(1f)
                )
                TravelModeChip(
                    label = "ترابری",
                    icon = Icons.Default.DirectionsBoat,
                    isSelected = route.travelMode == "transit",
                    onClick = { onModeSelect("transit") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                route.steps.forEachIndexed { idx, step ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${idx + 1}. ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TravelModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
