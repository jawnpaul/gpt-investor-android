import UIKit
import SwiftUI
import ComposeApp

/// Wraps the KMP Compose UI in a UIViewControllerRepresentable.
///
/// [mixpanelProvider], [youtubePlayerProvider], and [googleSignInProvider] are forwarded
/// directly to `mainViewController(...)` so that the Koin graph is fully configured
/// before the first composition.
struct ComposeView: UIViewControllerRepresentable {
    let mixpanelProvider: any MixpanelProvider
    let youtubePlayerProvider: any YoutubePlayerProvider
    let googleSignInProvider: any GoogleSignInProvider
    let storeKitProvider: any StoreKitProvider

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController(
            mixpanelProvider: mixpanelProvider,
            youtubePlayerProvider: youtubePlayerProvider,
            googleSignInProvider: googleSignInProvider,
            storeKitProvider: storeKitProvider
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let mixpanelProvider: any MixpanelProvider
    let youtubePlayerProvider: any YoutubePlayerProvider
    let googleSignInProvider: any GoogleSignInProvider
    let storeKitProvider: any StoreKitProvider

    var body: some View {
        ComposeView(
            mixpanelProvider: mixpanelProvider,
            youtubePlayerProvider: youtubePlayerProvider,
            googleSignInProvider: googleSignInProvider,
            storeKitProvider: storeKitProvider
        )
        .ignoresSafeArea()
    }
}
