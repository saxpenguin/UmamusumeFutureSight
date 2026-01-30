package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.saxpenguin.umamusumefuturesight.model.UserResources

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "資源計算機",
                style = MaterialTheme.typography.headlineSmall
            )

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

            Divider()

            Text(
                text = "統計結果",
                style = MaterialTheme.typography.titleMedium
            )

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
