package com.thejawnpaul.gptinvestor.features.billing.data

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Returns a Koin module that overrides [NoOpStoreKitProvider] with the real
 * Swift-side implementation. Pass a [SwiftStoreKitProvider] instance from iosApp
 * and include the result in the `modules(...)` call inside `koinConfiguration<GPTKoinApp>`.
 *
 * Usage from Swift (via MainViewController):
 *   MainViewControllerKt.mainViewController(
 *      ...,
 *      storeKitProvider: SwiftStoreKitProvider()
 *   )
 */

fun storeKitProviderModule(provider: StoreKitProvider): Module = module {
    single<StoreKitProvider> { provider }
}
