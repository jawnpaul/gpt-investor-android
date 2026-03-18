import UIKit
import SwiftUI
import ComposeApp

/// Wraps the KMP Compose UI in a UIViewControllerRepresentable.
///
/// Both [mixpanelProvider] and [youtubePlayerProvider] are forwarded directly to
/// `mainViewController(...)` so that the Koin graph is fully configured before the
/// first composition.
struct ComposeView: UIViewControllerRepresentable {
    let mixpanelProvider: any MixpanelProvider
    let youtubePlayerProvider: any YoutubePlayerProvider

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController(
            mixpanelProvider: mixpanelProvider,
            youtubePlayerProvider: youtubePlayerProvider
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let mixpanelProvider: any MixpanelProvider
    let youtubePlayerProvider: any YoutubePlayerProvider

    var body: some View {
        ComposeView(
            mixpanelProvider: mixpanelProvider,
            youtubePlayerProvider: youtubePlayerProvider
        )
        .ignoresSafeArea()
    }
}
