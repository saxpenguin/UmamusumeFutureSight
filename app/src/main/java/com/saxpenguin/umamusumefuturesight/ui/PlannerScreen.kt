package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.saxpenguin.umamusumefuturesight.model.UserResources
import com.saxpenguin.umamusumefuturesight.ui.components.NetworkImage
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                            label = "單抽券 (Single Tickets)",
                            value = uiState.resources.singleTickets,
                            onValueChange = { viewModel.updateSingleTickets(it) }
                        )
                        ResourceInput(
                            label = "十連券 (10-Pull Tickets)",
                            value = uiState.resources.tenPullTickets,
                            onValueChange = { viewModel.updateTenPullTickets(it) }
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("總計抽數 (Pulls):")
                    Text(
                        text = "${uiState.totalPulls}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("是否可保底 (Spark):")
                    Text(
                        text = if (uiState.canSpark) "是 (Yes)" else "否 (No)",
                        color = if (uiState.canSpark) Color.Green else Color.Red,
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
                        text = "預計到達該卡池時累積的資源",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                items(uiState.targetBanners) { projection ->
                    TargetProjectionCard(projection)
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
fun TargetProjectionCard(projection: BannerProjection) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = projection.banner.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            
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
                        text = "預計抽數",
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
                        text = "✓ 可保底 (Can Spark)",
                        color = Color.Green, // In real app use specific dark green or theme color
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
