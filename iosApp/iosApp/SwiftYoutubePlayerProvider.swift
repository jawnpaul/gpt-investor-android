import UIKit
import ComposeApp
import YouTubeiOSPlayerHelper

/// Swift implementation of the KMP `YoutubePlayerProvider` ObjC protocol.
///
/// Creates a `YTPlayerView` (from YouTubeiOSPlayerHelper) for each requested video,
/// attaching a `YTPlayerViewDelegate` to forward playback state changes.
/// The view is returned to Kotlin and embedded directly via `UIKitView` in Compose.
///
/// A `PlayerDelegate` instance is retained for the lifetime of each view via
/// `objc_setAssociatedObject`, ensuring delegate callbacks fire correctly without
/// any external retain chain.
final class SwiftYoutubePlayerProvider: NSObject, YoutubePlayerProvider {

    func createPlayerView(videoId: String, autoplay: Bool, showControls: Bool) -> UIView {
        let playerView = YTPlayerView()
        let delegate = PlayerDelegate()
        playerView.delegate = delegate

        // Retain the delegate for the lifetime of the player view.
        objc_setAssociatedObject(
            playerView,
            &AssociatedKeys.delegateKey,
            delegate,
            .OBJC_ASSOCIATION_RETAIN_NONATOMIC
        )

        let playerVars: [String: Any] = [
            "controls": showControls ? 1 : 0,
            "playsinline": 1,
            "autoplay": autoplay ? 1 : 0,
            "rel": 0
        ]
        playerView.load(withVideoId: videoId, playerVars: playerVars)
        return playerView
    }
}

// MARK: - Private

private struct AssociatedKeys {
    static var delegateKey: UInt8 = 0
}

private final class PlayerDelegate: NSObject, YTPlayerViewDelegate {
    func playerViewDidBecomeReady(_ playerView: YTPlayerView) {}

    func playerView(_ playerView: YTPlayerView, didChangeTo state: YTPlayerState) {}

    func playerView(_ playerView: YTPlayerView, receivedError error: YTPlayerError) {
        print("[SwiftYoutubePlayerProvider] Playback error: \(error)")
    }
}
