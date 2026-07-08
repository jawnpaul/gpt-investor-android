import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    let mixpanelProvider: any MixpanelProvider
    let youtubePlayerProvider: any YoutubePlayerProvider
    let googleSignInProvider: any GoogleSignInProvider

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController(
            mixpanelProvider: mixpanelProvider,
            youtubePlayerProvider: youtubePlayerProvider,
            googleSignInProvider: googleSignInProvider
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

/// iOS 26+ — one Compose view per tab, backed by a native UIViewController.
@available(iOS 26.0, *)
struct NativeNavComposeView: UIViewControllerRepresentable {
    let startRoute: String

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.tabViewController(startRoute: startRoute)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

/// Bridges Kotlin's NativeTabSwitcherRegistry to SwiftUI's TabView selection state.
///
/// Kotlin calls NativeTabSwitcherRegistry.switchTo(route) → handler.switchToTab(route)
/// → this class updates selectedTab on the main thread → TabView reacts.
@available(iOS 26.0, *)
class TabRouter: ObservableObject, NativeTabSwitchHandler {
    @Published var selectedTab: String = "home"

    func switchToTab(route: String) {
        DispatchQueue.main.async {
            switch route {
            case "home_tab_screen": self.selectedTab = "home"
            case "discover_tab_screen": self.selectedTab = "discover"
            case "watchlist_tab_screen": self.selectedTab = "watchlist"
            case "profile_tab_screen": self.selectedTab = "profile"
            default: break
            }
        }
    }
}

/// iOS 26+ — native SwiftUI TabView with the system liquid glass tab bar.
///
/// Tab selection is driven by TabRouter so that Kotlin code can switch tabs
/// by calling NativeTabSwitcherRegistry.switchTo(route) from any Compose screen.
@available(iOS 26.0, *)
struct NativeNavContentView: View {
    @StateObject private var tabRouter = TabRouter()

    var body: some View {
        TabView(selection: $tabRouter.selectedTab) {
            Tab("Home", systemImage: "house", value: "home") {
                NativeNavComposeView(startRoute: "home_tab_screen")
                    .ignoresSafeArea()
            }
            Tab("Discover", systemImage: "magnifyingglass", value: "discover") {
                NativeNavComposeView(startRoute: "discover_tab_screen")
                    .ignoresSafeArea()
            }
            Tab("Watchlist", systemImage: "bookmark", value: "watchlist") {
                NativeNavComposeView(startRoute: "watchlist_tab_screen")
                    .ignoresSafeArea()
            }
            Tab("Profile", systemImage: "person", value: "profile") {
                NativeNavComposeView(startRoute: "profile_tab_screen")
                    .ignoresSafeArea()
            }
        }
        .tabBarMinimizeBehavior(.automatic)
        .onAppear {
            NativeTabSwitcherRegistry.shared.handler = tabRouter
        }
        .onDisappear {
            NativeTabSwitcherRegistry.shared.handler = nil
        }
    }
}

struct ContentView: View {
    let mixpanelProvider: any MixpanelProvider
    let youtubePlayerProvider: any YoutubePlayerProvider
    let googleSignInProvider: any GoogleSignInProvider

    var body: some View {
        if #available(iOS 26.0, *) {
            NativeNavContentView()
        } else {
            ComposeView(
                mixpanelProvider: mixpanelProvider,
                youtubePlayerProvider: youtubePlayerProvider,
                googleSignInProvider: googleSignInProvider
            )
            .ignoresSafeArea()
        }
    }
}
