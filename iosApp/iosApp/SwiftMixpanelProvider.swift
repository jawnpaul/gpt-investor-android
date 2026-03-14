import ComposeApp
import Mixpanel

/// Swift implementation of the KMP `MixpanelProvider` ObjC protocol.
///
/// The KMP `analytics` module exposes the `MixpanelProvider` Kotlin interface as an ObjC
/// protocol (visible here as `MixpanelProvider` after `import ComposeApp`). This class
/// bridges every call through to the Mixpanel Swift SDK, which cannot be used from
/// Kotlin/Native directly because its main class is Swift-only (not ObjC-visible).
///
/// Lifecycle:
///   - `init(token:)` calls `Mixpanel.initialize` exactly once; hold a single instance for
///     the app lifetime (created in `iOSApp` and passed to `mainViewController`).
///   - All protocol methods are forwarded to `Mixpanel.mainInstance()`.
///
/// Registration:
///   This instance is passed to `MainViewControllerKt.mainViewController(mixpanelProvider:)`,
///   which includes it in the Koin graph via `mixpanelProviderModule(provider)`, overriding
///   the default `NoOpMixpanelProvider` registered by @ComponentScan.
final class SwiftMixpanelProvider: NSObject, MixpanelProvider {

    private let mixpanel: MixpanelInstance

    /// Initialises the Mixpanel SDK and retains the main instance.
    ///
    /// - Parameter token: Project token from your Mixpanel dashboard.
    ///   Use `MIXPANEL_DEV_TOKEN` (local.properties) for debug builds and
    ///   `MIXPANEL_PROD_TOKEN` for release builds.
    init(token: String) {
        Mixpanel.initialize(token: token, trackAutomaticEvents: false)
        self.mixpanel = Mixpanel.mainInstance()
        super.init()
    }

    // MARK: - MixpanelProvider

    func logEvent(eventName: String, params: [String: Any]) {
        mixpanel.track(event: eventName, properties: mixpanelProperties(from: params))
    }

    func logViewEvent(screenName: String) {
        mixpanel.track(event: "Screen View", properties: ["screen_name": screenName])
    }

    /// Maps to Mixpanel's `identify` + optional people profile update.
    func identifyUser(userId: String, params: [String: Any]) {
        mixpanel.identify(distinctId: userId)
        let peopleProps = mixpanelProperties(from: params)
        if !peopleProps.isEmpty {
            mixpanel.people.set(properties: peopleProps)
        }
    }

    func resetUser() {
        mixpanel.reset()
    }

    // MARK: - Private helpers

    /// Filters `[String: Any]` values coming across the ObjC bridge down to only
    /// those that conform to `MixpanelType` (String, NSNumber, NSNull, NSDate, NSURL,
    /// and their array/dictionary equivalents). Any Kotlin-boxed type that does not
    /// bridge cleanly is silently dropped rather than causing a runtime crash.
    private func mixpanelProperties(from map: [String: Any]) -> [String: MixpanelType] {
        map.compactMapValues { $0 as? MixpanelType }
    }
}
