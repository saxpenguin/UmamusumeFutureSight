package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxpenguin.umamusumefuturesight.data.BannerRepository
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.ui.components.NetworkImage
import com.saxpenguin.umamusumefuturesight.ui.components.Badge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.saxpenguin.umamusumefuturesight.R
import com.saxpenguin.umamusumefuturesight.model.SupportCardType

data class BannerDetailUiState(
    val banner: Banner? = null,
    val offsetDays: Long = 490,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BannerDetailViewModel @Inject constructor(
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BannerDetailUiState(isLoading = true))
    val uiState: StateFlow<BannerDetailUiState> = _uiState.asStateFlow()

    fun loadBanner(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Simulate loading if needed or fetching single item logic
                val banners = bannerRepository.getBanners()
                val banner = banners.find { it.id == id }
                
                if (banner != null) {
                    _uiState.update { it.copy(banner = banner, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "找不到卡池資料") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannerDetailScreen(
    bannerId: String,
    onBackClick: () -> Unit,
    viewModel: BannerDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(bannerId) {
        viewModel.loadBanner(bannerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("卡池詳情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "發生錯誤: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadBanner(bannerId) }) {
                        Text("重試")
                    }
                }
            } else {
                uiState.banner?.let { banner ->
                    BannerDetailContent(
                        banner = banner,
                        offsetDays = uiState.offsetDays,
                        onLinkClick = { url ->
                            uriHandler.openUri(url)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BannerDetailContent(
    banner: Banner,
    offsetDays: Long,
    onLinkClick: (String) -> Unit
) {
    val twStartDate = banner.getTwStartDate(offsetDays)
    val twEndDate = banner.getTwEndDate(offsetDays)
    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), twStartDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image
        if (banner.featuredCards.isNotEmpty()) {
            val displayCount = if (banner.featuredCards.size == 1) 1 else 2
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp), // Height matches original single image
                horizontalArrangement = Arrangement.Center
            ) {
                banner.featuredCards.take(displayCount).forEach { cardInfo ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f) // Square
                            .fillMaxHeight()
                            .padding(horizontal = 4.dp)
                    ) {
                        if (cardInfo.imageUrl != null) {
                            NetworkImage(
                                url = cardInfo.imageUrl,
                                contentDescription = cardInfo.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No Image")
                            }
                        }

                        // Type Icon for Support Cards
                        if (banner.type == BannerType.SUPPORT_CARD && cardInfo.type != SupportCardType.UNKNOWN) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                val iconRes = when (cardInfo.type) {
                                    SupportCardType.SPEED -> R.drawable.utx_ico_obtain_speed
                                    SupportCardType.STAMINA -> R.drawable.utx_ico_obtain_stamina
                                    SupportCardType.POWER -> R.drawable.utx_ico_obtain_power
                                    SupportCardType.GUTS -> R.drawable.utx_ico_obtain_endurance
                                    SupportCardType.WISDOM -> R.drawable.utx_ico_obtain_wise
                                    SupportCardType.FRIEND -> R.drawable.utx_ico_obtain_friend
                                    SupportCardType.GROUP -> R.drawable.utx_ico_obtain_group
                                    else -> null
                                }

                                if (iconRes != null) {
                                    Icon(
                                        painter = painterResource(id = iconRes),
                                        contentDescription = cardInfo.type.name,
                                        modifier = Modifier.size(32.dp),
                                        tint = Color.Unspecified
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (banner.imageUrl != null) {
            NetworkImage(
                url = banner.imageUrl,
                contentDescription = banner.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image Available")
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Type Badge and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Badge(type = banner.type)

                val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), twEndDate)
                if (daysUntil > 0) {
                        Text(
                        text = "還有 $daysUntil 天",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else if (daysRemaining >= 0) {
                    Text(
                        text = "進行中 (剩餘 $daysRemaining 天)",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                        Text(
                        text = "已結束",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Title
            Text(
                text = banner.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Dates Section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        icon = Icons.Default.DateRange,
                        label = "日版活動期間",
                        value = "${banner.jpStartDate} ~ ${banner.jpEndDate}"
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(
                        icon = Icons.Default.DateRange,
                        label = "台版預測期間",
                        value = "$twStartDate ~ $twEndDate",
                        isHighlight = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // External Links
            Button(
                onClick = {
                    banner.linkUrl?.let { url ->
                        onLinkClick(url)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = banner.linkUrl != null
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("查看 Wiki 詳細資料")
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
