import SwiftUI
import ComposeApp

@main
struct GPTInvestorApp: App {
    init() {
        IosKoinApplicationKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ComposeRootView()
                .ignoresSafeArea()
        }
    }
}

struct ComposeRootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // no-op
    }
}
