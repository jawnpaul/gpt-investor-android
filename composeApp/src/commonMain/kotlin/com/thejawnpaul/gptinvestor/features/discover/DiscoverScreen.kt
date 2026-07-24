package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.curated_for_you
import com.thejawnpaul.gptinvestor.daily_learn_tidbit
import com.thejawnpaul.gptinvestor.discover
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeErrorCard
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeSearchBar
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeSectionHeader
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeTopPicksSection
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.HomeTidbitSection
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.today_s_lesson_didn_t_load
import com.thejawnpaul.gptinvestor.top_picks_today
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoverScreen(state: DiscoveryScreenState, onEvent: (DiscoveryEvent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column(
                modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.discover),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.statusBarsPadding().fillMaxWidth()
                )
                if (state.isGuestSession) {
                    TopGuestLabel(modifier = Modifier.fillMaxWidth(), onClick = {
                        onEvent(DiscoveryEvent.GoToSignUp)
                    })
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                item {
                    // Search
                    HomeSearchBar(modifier = Modifier.padding(horizontal = 16.dp), onClick = {
                        onEvent(DiscoveryEvent.GoToSearch)
                    })
                }

                item {
                    // Top picks
                    HomeSectionHeader(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        emoji = "🔖",
                        label = stringResource(Res.string.top_picks_today),
                        title = stringResource(Res.string.curated_for_you),
                        onSeeAll = {
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HomeTopPicksSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        view = state.topPicksView,
                        onRetry = { onEvent(DiscoveryEvent.RetryTopPicks) },
                        onClickPick = { onEvent(DiscoveryEvent.ClickTopPick(id = it)) }
                    )
                }

                item {
                    // Tidbit
                    Spacer(modifier = Modifier.height(12.dp))
                    when {
                        state.homeTidbitView.error != null -> {
                            HomeErrorCard(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                message = stringResource(Res.string.today_s_lesson_didn_t_load),
                                onRetry = { onEvent(DiscoveryEvent.RetryTidbit) }
                            )
                        }

                        else -> {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = stringResource(Res.string.daily_learn_tidbit).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalGPTInvestorColors.current.textColors.secondary50
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HomeTidbitSection(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                tidbit = state.homeTidbitView,
                                onClick = { onEvent(DiscoveryEvent.ClickTidbit(id = it)) },
                                isLoading = state.homeTidbitView.loading
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DiscoverScreenTopPicksPreview() {
    GPTInvestorTheme {
        DiscoverScreen(
            state = DiscoveryScreenState(
                sectors = listOf(
                    SectorInput.AllSector,
                    SectorInput.CustomSector("Top Picks", "top-picks"),
                    SectorInput.CustomSector("Technology", "technology")
                ),
                topPicks = listOf(
                    TopPickPresentation(
                        id = "1",
                        ticker = "JPM",
                        companyName = "JP Morgan Chase & Co.",
                        rationale = "JPM is leveraging its massive scale to lead the banking " +
                            "sector's digital revolution.",
                        metrics = emptyList(),
                        risks = emptyList(),
                        confidenceScore = 80,
                        isSaved = false,
                        percentageChange = 1.2f,
                        imageUrl = "",
                        currentPrice = 185.0f
                    )
                ),
                selected = SectorInput.CustomSector("Top Picks", "top-picks")
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun DiscoverScreenCompaniesPreview() {
    GPTInvestorTheme {
        DiscoverScreen(
            state = DiscoveryScreenState(
                sectors = listOf(
                    SectorInput.AllSector,
                    SectorInput.CustomSector("Top Picks", "top-picks"),
                    SectorInput.CustomSector("Technology", "technology")
                ),
                selected = SectorInput.AllSector
            ),
            onEvent = {}
        )
    }
}
