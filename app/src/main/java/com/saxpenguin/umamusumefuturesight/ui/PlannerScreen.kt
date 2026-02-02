package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.model.UserResources
import com.saxpenguin.umamusumefuturesight.ui.components.NetworkImage
import com.saxpenguin.umamusumefuturesight.ui.components.Badge
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTargetBanners()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("規劃工具") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "資源計算機",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ResourceInput(
                            label = "持有寶石 (Jewels)",
                            value = uiState.resources.jewels,
                            onValueChange = { viewModel.updateJewels(it) }
                        )
                        ResourceInput(
                            label = "賽馬娘轉蛋券 (Character Tickets)",
                            value = uiState.resources.characterTickets,
                            onValueChange = { viewModel.updateCharacterTickets(it) }
                        )
                        ResourceInput(
                            label = "支援卡轉蛋券 (Support Card Tickets)",
                            value = uiState.resources.singleTickets,
                            onValueChange = { viewModel.updateSingleTickets(it) }
                        )
                        
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        
                        Text(
                            text = "每日預估獲得寶石",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        // Player Type Selection Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                             SuggestionChip(
                                onClick = { viewModel.updateDailyJewelIncome(600) },
                                label = { Text("無課金") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (uiState.resources.dailyJewelIncome == 600) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                )
                            )
                            SuggestionChip(
                                onClick = { viewModel.updateDailyJewelIncome(650) },
                                label = { Text("月卡/通行證") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (uiState.resources.dailyJewelIncome == 650) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                             SuggestionChip(
                                onClick = { viewModel.updateDailyJewelIncome(700) },
                                label = { Text("雙卡皆買") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (uiState.resources.dailyJewelIncome == 700) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                )
                            )
                        }

                        ResourceInput(
                            label = "自訂每日寶石收入",
                            value = uiState.resources.dailyJewelIncome,
                            onValueChange = { viewModel.updateDailyJewelIncome(it) }
                        )
                    }
                }
            }

            item {
                Divider()
            }

            item {
                Text(
                    text = "目前持有",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Character Pulls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("馬娘池總計抽數:")
                    Text(
                        text = "${uiState.totalCharacterPulls}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("馬娘池保底:")
                    Text(
                        text = if (uiState.canSparkCharacter) "是" else "否",
                        color = if (uiState.canSparkCharacter) Color.Green else Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                // Support Pulls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("支援卡池總計抽數:")
                    Text(
                        text = "${uiState.totalSupportPulls}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                 Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("支援卡池保底:")
                    Text(
                        text = if (uiState.canSparkSupport) "是" else "否",
                        color = if (uiState.canSparkSupport) Color.Green else Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            
            item {
                Divider()
            }

            if (uiState.targetBanners.isNotEmpty()) {
                item {
                    Text(
                        text = "目標池預測 (Target Projections)",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "預計到達該卡池時累積的資源 (含對應轉蛋券)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                items(uiState.targetBanners) { projection ->
                    TargetProjectionCard(
                        projection = projection,
                        onRemoveClick = { viewModel.removeTarget(it) }
                    )
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "尚未設定追蹤目標\n請到列表頁面加入追蹤",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TargetProjectionCard(
    projection: BannerProjection,
    onRemoveClick: (String) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Text(
                    text = projection.banner.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(type = projection.banner.type)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onRemoveClick(projection.banner.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "取消追蹤",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "預計日期: ${projection.banner.getTwStartDate()}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "預計持有寶石",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${projection.projectedResources.jewels}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                     Text(
                        text = "可用總抽數",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "${projection.projectedPulls}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (projection.canSpark) {
                Surface(
                    color = Color.Green.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "✓ 可保底",
                        color = Color.Green, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                 Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "⚠ 資源不足保底",
                        color = Color.Red,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ResourceInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = if (value == 0) "" else value.toString(),
        onValueChange = { str ->
            if (str.isEmpty()) {
                onValueChange(0)
            } else if (str.all { it.isDigit() }) {
                onValueChange(str.toInt())
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
