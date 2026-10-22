package com.thejawnpaul.gptinvestor.core.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class TokenStorageImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var tokenStorage: TokenStorageImpl

    @Before
    fun setUp() {
        val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
            tempFolder.newFile("test.preferences_pb").also { it.delete() }
        }
        tokenStorage = TokenStorageImpl(DataStoreAppPreferences(dataStore))
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun `should keep user token when guest refresh finishes after login`() {
        tokenStorage.saveAccessToken("expired-guest-token")
        val tokenSeenByGuestRefresh = tokenStorage.getAccessToken()

        tokenStorage.saveAccessToken("user-access-token")
        tokenStorage.saveRefreshToken("user-refresh-token")
        val replaced = tokenStorage.replaceAccessToken(tokenSeenByGuestRefresh, "new-guest-token")

        assertThat(replaced).isFalse()
        assertThat(tokenStorage.getAccessToken()).isEqualTo("user-access-token")
        assertThat(tokenStorage.getRefreshToken()).isEqualTo("user-refresh-token")
    }

    @Test
    fun `should save new guest token when stored token is still the expired one`() {
        tokenStorage.saveAccessToken("expired-guest-token")

        val replaced = tokenStorage.replaceAccessToken("expired-guest-token", "new-guest-token")

        assertThat(replaced).isTrue()
        assertThat(tokenStorage.getAccessToken()).isEqualTo("new-guest-token")
    }
}
