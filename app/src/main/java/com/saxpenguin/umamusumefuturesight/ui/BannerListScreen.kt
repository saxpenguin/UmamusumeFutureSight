package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.ui.components.NetworkImage
import com.saxpenguin.umamusumefuturesight.ui.theme.UmamusumeFutureSightTheme
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

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
                    onBannerClick = onBannerClick
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
    onBannerClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(banners) { banner ->
            BannerCard(
                banner = banner,
                offsetDays = offsetDays,
                onClick = { onBannerClick(banner.id) }
            )
        }
    }
}

@Composable
fun BannerCard(
    banner: Banner,
    offsetDays: Long,
    onClick: () -> Unit
) {
    val twStartDate = banner.getTwStartDate(offsetDays)
    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), twStartDate)
    
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            if (banner.imageUrl != null) {
                NetworkImage(
                    url = banner.imageUrl,
                    contentDescription = banner.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Badge(type = banner.type)
                    if (daysUntil >= 0) {
                         Text(
                            text = "還有 $daysUntil 天",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                         Text(
                            text = "已結束 / 進行中",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = banner.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
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
fun Badge(type: BannerType) {
    val (color, text) = when (type) {
        BannerType.CHARACTER -> MaterialTheme.colorScheme.secondary to "馬娘"
        BannerType.SUPPORT_CARD -> MaterialTheme.colorScheme.tertiary to "支援卡"
    }
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
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

@Preview(showBackground = true)
@Composable
fun BannerListScreenPreview() {
    UmamusumeFutureSightTheme {
        BannerListScreen(onBannerClick = {})
    }
}
