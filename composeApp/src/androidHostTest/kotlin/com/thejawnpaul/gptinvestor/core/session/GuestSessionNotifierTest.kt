package com.thejawnpaul.gptinvestor.core.session

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GuestSessionNotifierTest {

    @Test
    fun `single notify emits one event`() = runTest {
        val notifier = GuestSessionNotifier()
        notifier.signal.test {
            notifier.notify(GuestSessionEvent(GuestSessionReason.Expired, GuestSessionSource.Request))
            assertThat(awaitItem().reason).isEqualTo(GuestSessionReason.Expired)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple rapid notifies produce at most two events due to buffer`() = runTest {
        val notifier = GuestSessionNotifier()
        // fire 10 parallel notifies
        repeat(10) {
            async {
                notifier.notify(
                    GuestSessionEvent(GuestSessionReason.Expired, GuestSessionSource.Request)
                )
            }
        }
        notifier.signal.test {
            val first = awaitItem()
            assertThat(first.reason).isEqualTo(GuestSessionReason.Expired)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `notify with limit reason carries correct reason`() = runTest {
        val notifier = GuestSessionNotifier()
        notifier.signal.test {
            notifier.notify(GuestSessionEvent(GuestSessionReason.Limit, GuestSessionSource.Stream, ticker = "AAPL"))
            val event = awaitItem()
            assertThat(event.reason).isEqualTo(GuestSessionReason.Limit)
            assertThat(event.source).isEqualTo(GuestSessionSource.Stream)
            assertThat(event.ticker).isEqualTo("AAPL")
            cancelAndIgnoreRemainingEvents()
        }
    }
}
