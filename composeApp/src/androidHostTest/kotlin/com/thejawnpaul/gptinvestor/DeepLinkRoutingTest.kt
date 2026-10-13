package com.thejawnpaul.gptinvestor

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.navigation.Screen
import com.thejawnpaul.gptinvestor.features.guest.presentation.GuestScreen
import org.junit.Test

class DeepLinkRoutingTest {

    @Test
    fun `price alert link opens company detail for the ticker`() {
        val route = getNavigationRouteFromDeepLink("app://gpt-investor/company_detail_screen/AAPL", isGuest = false)

        assertThat(route).isEqualTo(Screen.CompanyDetailScreen.createRoute("AAPL"))
    }

    @Test
    fun `price alert link opens guest company detail for guests`() {
        val route = getNavigationRouteFromDeepLink("app://gpt-investor/company_detail_screen/AAPL", isGuest = true)

        assertThat(route).isEqualTo(GuestScreen.GuestCompanyDetail.createRoute("AAPL"))
    }

    @Test
    fun `company link without a ticker falls back to home`() {
        val route = getNavigationRouteFromDeepLink("app://gpt-investor/company_detail_screen/", isGuest = false)

        assertThat(route).isEqualTo(Screen.HomeTabScreen.route)
    }

    @Test
    fun `digest link opens digest detail`() {
        val route = getNavigationRouteFromDeepLink("digest_detail_screen", isGuest = false)

        assertThat(route).isEqualTo(Screen.DigestDetailScreen.route)
    }

    @Test
    fun `digest link sends guests home`() {
        val route = getNavigationRouteFromDeepLink("digest_detail_screen", isGuest = true)

        assertThat(route).isEqualTo(GuestScreen.GuestHomeTab.route)
    }

    @Test
    fun `tidbit link still opens tidbit detail`() {
        val route = getNavigationRouteFromDeepLink("app://gpt-investor/tidbit_detail_screen/42", isGuest = false)

        assertThat(route).isEqualTo(Screen.TidbitDetailScreen.createRoute(tidbitId = "42"))
    }
}
