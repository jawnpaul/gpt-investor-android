import UIKit
import SwiftUI
import ComposeApp

/// Wraps the KMP Compose UI in a UIViewControllerRepresentable.
///
/// [mixpanelProvider] is forwarded directly to `mainViewController(mixpanelProvider:)`
/// so that the Koin graph is fully configured before the first composition.
struct ComposeView: UIViewControllerRepresentable {
    let mixpanelProvider: any MixpanelProvider

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController(mixpanelProvider: mixpanelProvider)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let mixpanelProvider: any MixpanelProvider

    var body: some View {
        ComposeView(mixpanelProvider: mixpanelProvider)
            .ignoresSafeArea()
    }
}
