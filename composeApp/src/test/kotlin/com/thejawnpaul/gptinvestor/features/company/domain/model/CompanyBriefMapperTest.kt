package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.BriefSectionRemote
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyNews
import org.junit.Test

class CompanyBriefMapperTest {

    private val fixedNow = 1_716_000_000L

    @Test
    fun `should map a fully-populated payload to all sections`() {
        val response = CompanyDetailRemoteResponse(
            ticker = "AAPL",
            name = "Apple Inc.",
            about = "Apple makes iPhones and other devices.",
            marketCap = 2_830_000_000_000L,
            peRatio = 29.4f,
            change = 1.42f,
            revenue = 383_000_000_000L,
            price = 182.30f,
            imageUrl = "http://example.com/aapl.png",
            sentiment = "BULLISH",
            sentimentSummary = "Services growth keeps the long-term outlook strong.",
            dividendYield = 0.51f,
            revenueGrowth = 4.2f,
            marketCapInsight = "Among the largest in the world",
            marketCapInsightTone = "NEUTRAL",
            peRatioInsight = "Fairly valued",
            peRatioInsightTone = "NEUTRAL",
            revenueGrowthInsight = "Growing steadily",
            revenueGrowthInsightTone = "POSITIVE",
            dividendYieldInsight = "Small but reliable",
            dividendYieldInsightTone = "NEUTRAL",
            risk = BriefSectionRemote(title = "The Risk", body = "iPhone concentration risk."),
            opportunity = BriefSectionRemote(title = "The Opportunity", body = "Services margins."),
            news = listOf(
                CompanyNews(
                    link = "https://reuters.example/article",
                    providerPublishTime = fixedNow - 2 * 3_600L,
                    publisher = "Reuters",
                    relatedTickers = listOf("AAPL"),
                    thumbNail = null,
                    title = "Apple unveils on-device AI features",
                    type = "STORY",
                    id = "n-1",
                    whatItMeans = "Reason to upgrade phones, lifting revenue.",
                    whatItMeansSentiment = "POSITIVE"
                )
            )
        )

        val brief = response.toBrief(now = fixedNow)

        assertThat(brief.ticker).isEqualTo("AAPL")
        assertThat(brief.name).isEqualTo("Apple Inc.")
        assertThat(brief.logoUrl).isEqualTo("https://example.com/aapl.png")
        assertThat(brief.price).isEqualTo(182.30f)
        assertThat(brief.change).isEqualTo(1.42f)
        assertThat(brief.sentiment).isEqualTo(BriefSentiment.Bullish)
        assertThat(brief.sentimentSummary).isEqualTo("Services growth keeps the long-term outlook strong.")
        assertThat(brief.summary).isEqualTo("Apple makes iPhones and other devices.")
        assertThat(brief.keyNumbers.map { it.key }).containsExactly(
            KeyNumberType.MarketCap,
            KeyNumberType.PeRatio,
            KeyNumberType.RevenueGrowth,
            KeyNumberType.DividendYield
        ).inOrder()
        assertThat(brief.keyNumbers[0].value).startsWith("$")
        assertThat(brief.keyNumbers[0].insight).isEqualTo("Among the largest in the world")
        assertThat(brief.keyNumbers[2].value).isEqualTo("+4.2%")
        assertThat(brief.keyNumbers[2].tone).isEqualTo(BriefTone.Positive)
        assertThat(brief.keyNumbers[3].value).isEqualTo("0.51%")
        assertThat(brief.news).hasSize(1)
        assertThat(brief.news[0].publishedRelative).isEqualTo("2h ago")
        assertThat(brief.news[0].tone).isEqualTo(BriefTone.Positive)
        assertThat(brief.risk?.title).isEqualTo("The Risk")
        assertThat(brief.opportunity?.body).isEqualTo("Services margins.")
    }

    @Test
    fun `should hide null sections and produce empty key numbers`() {
        val response = CompanyDetailRemoteResponse(
            ticker = "XYZ",
            name = "Acme Corp.",
            about = null,
            sentiment = null,
            sentimentSummary = null,
            marketCap = null,
            peRatio = null,
            revenueGrowth = null,
            dividendYield = null,
            risk = null,
            opportunity = null,
            news = null
        )

        val brief = response.toBrief(now = fixedNow)

        assertThat(brief.summary).isNull()
        assertThat(brief.sentiment).isNull()
        assertThat(brief.sentimentSummary).isNull()
        assertThat(brief.keyNumbers).isEmpty()
        assertThat(brief.news).isEmpty()
        assertThat(brief.risk).isNull()
        assertThat(brief.opportunity).isNull()
    }

    @Test
    fun `should derive tone from sign when backend tone is missing`() {
        val gain = CompanyDetailRemoteResponse(
            ticker = "X",
            revenueGrowth = 5.5f,
            revenueGrowthInsightTone = null
        )
        val loss = CompanyDetailRemoteResponse(
            ticker = "Y",
            revenueGrowth = -2.0f,
            revenueGrowthInsightTone = null
        )

        val gainBrief = gain.toBrief(now = fixedNow)
        val lossBrief = loss.toBrief(now = fixedNow)

        assertThat(gainBrief.keyNumbers.single().tone).isEqualTo(BriefTone.Positive)
        assertThat(lossBrief.keyNumbers.single().tone).isEqualTo(BriefTone.Negative)
    }

    @Test
    fun `should drop a section whose body is blank`() {
        val response = CompanyDetailRemoteResponse(
            ticker = "X",
            risk = BriefSectionRemote(title = "The Risk", body = "  "),
            opportunity = BriefSectionRemote(title = "", body = "Real opportunity body.")
        )

        val brief = response.toBrief(now = fixedNow)

        assertThat(brief.risk).isNull()
        assertThat(brief.opportunity?.body).isEqualTo("Real opportunity body.")
        assertThat(brief.opportunity?.title).isEmpty()
    }
}
