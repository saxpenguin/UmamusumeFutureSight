package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.ui.components.NetworkImage
import com.saxpenguin.umamusumefuturesight.ui.components.Badge
import com.saxpenguin.umamusumefuturesight.ui.theme.UmamusumeFutureSightTheme
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import com.saxpenguin.umamusumefuturesight.model.BannerCardInfo
import com.saxpenguin.umamusumefuturesight.model.SupportCardType
import com.saxpenguin.umamusumefuturesight.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannerListScreen(

    onBannerClick: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {

            TopAppBar(
                title = { Text("賽馬娘未來視") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "排序",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("日期 (舊 -> 新)") },
                            onClick = {
                                viewModel.setSort(SortOption.DATE_ASC)
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (uiState.sortOption == SortOption.DATE_ASC) {
                                    Text("✓")
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("日期 (新 -> 舊)") },
                            onClick = {
                                viewModel.setSort(SortOption.DATE_DESC)
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (uiState.sortOption == SortOption.DATE_DESC) {
                                    Text("✓")
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("類型") },
                            onClick = {
                                viewModel.setSort(SortOption.TYPE)
                                showSortMenu = false
                            },
                            leadingIcon = {
                                if (uiState.sortOption == SortOption.TYPE) {
                                    Text("✓")
                                }
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(if (uiState.showExpired) "隱藏已過期" else "顯示已過期") },
                            onClick = {
                                viewModel.toggleShowExpired()
                                // Keep menu open or close it? usually toggle settings might keep it open, but simple behavior is close
                                // Let's keep it consistent with other actions and close it, or user can re-open.
                                // Actually, for a toggle, it's often nicer to see the change. But let's close for now.
                            },
                            leadingIcon = {
                                if (uiState.showExpired) {
                                    Text("✓")
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FilterChips(
                currentFilter = uiState.filterType,
                onFilterSelected = { viewModel.setFilter(it) }
            )
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "發生錯誤: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("重試")
                        }
                    }
                }
            } else {
                BannerList(
                    banners = uiState.banners,
                    offsetDays = uiState.offsetDays,
                    onBannerClick = onBannerClick,
                    onTargetClick = { viewModel.toggleTarget(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    currentFilter: BannerType?,
    onFilterSelected: (BannerType?) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("全部") }
        )
        FilterChip(
            selected = currentFilter == BannerType.CHARACTER,
            onClick = { onFilterSelected(BannerType.CHARACTER) },
            label = { Text("馬娘") }
        )
        FilterChip(
            selected = currentFilter == BannerType.SUPPORT_CARD,
            onClick = { onFilterSelected(BannerType.SUPPORT_CARD) },
            label = { Text("支援卡") }
        )
    }
}

@Composable
fun BannerList(
    banners: List<Banner>,
    offsetDays: Long,
    onBannerClick: (String) -> Unit,
    onTargetClick: (Banner) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(banners) { banner ->
            BannerCard(
                banner = banner,
                offsetDays = offsetDays,
                onClick = { onBannerClick(banner.id) },
                onTargetClick = { onTargetClick(banner) }
            )
        }
    }
}

@Composable
fun BannerCard(
    banner: Banner,
    offsetDays: Long,

    onClick: () -> Unit,
    onTargetClick: () -> Unit
) {
    val twStartDate = banner.getTwStartDate(offsetDays)
    val twEndDate = banner.getTwEndDate(offsetDays)
    val today = LocalDate.now()
    val daysUntil = ChronoUnit.DAYS.between(today, twStartDate)
    
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            // New Layout: Display multiple cards if available
            if (banner.featuredCards.isNotEmpty()) {
                val displayCount = if (banner.featuredCards.size == 1) 1 else 2
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.Center // Center the cards
                ) {
                    banner.featuredCards.take(displayCount).forEach { cardInfo ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f) // 1:1 Aspect Ratio (Square)
                                .fillMaxHeight()
                                .padding(horizontal = 4.dp) // Add spacing between cards
                        ) {
                            if (cardInfo.imageUrl != null) {
                                NetworkImage(
                                    url = cardInfo.imageUrl,
                                    contentDescription = cardInfo.name,
                                    contentScale = ContentScale.Crop, // Always crop to fill the fixed box
                                    alignment = Alignment.TopCenter,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Fallback if no image
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant) // Add background for fallback
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cardInfo.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 3
                                    )
                                }
                            }
                            
                            // Name Overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = cardInfo.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }

                            // Type Display Overlay (Top Right)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            ) {
                                if (banner.type == BannerType.SUPPORT_CARD && cardInfo.type != SupportCardType.UNKNOWN) {
                                    TypeIcon(
                                        type = cardInfo.type,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    // Show Badge for Characters or Supports without specific type icon
                                    Badge(type = banner.type)
                                }
                            }
                        }
                    }
                }
            } else if (banner.imageUrl != null) {
                // Fallback to single main image if featuredCards is empty
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    NetworkImage(
                        url = banner.imageUrl,
                        contentDescription = banner.name,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Overlay badge on main banner image too
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Badge(type = banner.type)
                    }
                }
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                     if (daysUntil > 0) {
                         Text(
                            text = "還有 $daysUntil 天",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (today.isAfter(twEndDate)) {
                         Text(
                            text = "已結束",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        val daysRemaining = ChronoUnit.DAYS.between(today, twEndDate)
                         Text(
                            text = "進行中 (剩餘 $daysRemaining 天)",
                            color = MaterialTheme.colorScheme.tertiary, // Use a highlight color for active
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onTargetClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (banner.isTarget) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (banner.isTarget) "取消追蹤" else "追蹤",
                            tint = if (banner.isTarget) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DateInfo(label = "日版", date = banner.jpStartDate)
                    DateInfo(label = "台版(預測)", date = twStartDate, isHighlight = true)
                }
            }
        }
    }
}


@Composable
fun DateInfo(label: String, date: LocalDate, isHighlight: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = date.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

@Composable
fun TypeIcon(type: SupportCardType, modifier: Modifier = Modifier) {
    val iconRes = when (type) {
        SupportCardType.SPEED -> R.drawable.utx_ico_obtain_speed
        SupportCardType.STAMINA -> R.drawable.utx_ico_obtain_stamina
        SupportCardType.POWER -> R.drawable.utx_ico_obtain_power
        SupportCardType.GUTS -> R.drawable.utx_ico_obtain_endurance // Assuming endurance = guts based on file list
        SupportCardType.WISDOM -> R.drawable.utx_ico_obtain_wise
        SupportCardType.FRIEND -> R.drawable.utx_ico_obtain_friend
        SupportCardType.GROUP -> R.drawable.utx_ico_obtain_group
        else -> null
    }

    if (iconRes != null) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = type.name,
            modifier = modifier,
            tint = Color.Unspecified
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BannerListScreenPreview() {
    UmamusumeFutureSightTheme {
        BannerListScreen(onBannerClick = {})
    }
}
