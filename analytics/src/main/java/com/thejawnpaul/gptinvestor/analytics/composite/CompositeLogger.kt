package com.thejawnpaul.gptinvestor.analytics.composite

import com.thejawnpaul.gptinvestor.analytics.Analytics
import com.thejawnpaul.gptinvestor.analytics.di.FirebaseAnalytics
import com.thejawnpaul.gptinvestor.analytics.di.MixpanelAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompositeLogger private constructor(private val loggers: Map<Analytics.Provider, Analytics>) :
    Analytics {

    private var activeProviders = setOf(Analytics.Provider.ALL)

    fun setActiveProviders(providers: Set<Analytics.Provider>) {
        activeProviders = providers
    }

    private fun isProviderActive(provider: Analytics.Provider): Boolean {
        return activeProviders.contains(Analytics.Provider.ALL) || activeProviders.contains(provider)
    }

    override fun logDefaultPrompt(promptTitle: String, promptQuery: String) {
        loggers.forEach { (provider, logger) ->
            if (isProviderActive(provider)) {
                logger.logDefaultPrompt(promptTitle, promptQuery)
            }
        }
    }

    override fun logSelectedCompany(companyTicker: String) {
        loggers.forEach { (provider, logger) ->
            if (isProviderActive(provider)) {
                logger.logSelectedCompany(companyTicker)
            }
        }
    }

    override fun logCompanyIdentified(companyTicker: String) {
        loggers.forEach { (provider, logger) ->
            if (isProviderActive(provider)) {
                logger.logCompanyIdentified(companyTicker)
            }
        }
    }

    override fun logShareEvent(contentType: String, contentName: String) {
        loggers.forEach { (provider, logger) ->
            if (isProviderActive(provider)) {
                logger.logShareEvent(contentType, contentName)
            }
        }
    }

    override fun logSaveEvent(contentType: String, contentName: String) {
        loggers.forEach { (provider, logger) ->
            if (isProviderActive(provider)) {
                logger.logSaveEvent(contentType, contentName)
            }
        }
    }

    override fun logTopPickSelected(companyTicker: String, companyName: String) {
        loggers.forEach { (provider, logger) ->
            if (isProviderActive(provider)) {
                logger.logTopPickSelected(companyTicker, companyName)
            }
        }
    }

    class Builder @Inject constructor(
        @FirebaseAnalytics private val firebaseLogger: Analytics,
        @MixpanelAnalytics private val mixpanelLogger: Analytics
    ) {
        private val loggers = mutableMapOf<Analytics.Provider, Analytics>()

        fun withFirebase(): Builder {
            loggers[Analytics.Provider.FIREBASE] = firebaseLogger
            return this
        }

        fun withMixpanel(): Builder {
            loggers[Analytics.Provider.MIXPANEL] = mixpanelLogger
            return this
        }

        fun withAllLoggers(): Builder {
            return withFirebase().withMixpanel()
        }

        fun build(): CompositeLogger {
            require(loggers.isNotEmpty()) { "At least one logger must be added" }
            return CompositeLogger(loggers)
        }
    }

}