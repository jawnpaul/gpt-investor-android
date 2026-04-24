import ComposeApp
import FirebaseCore
import GoogleSignIn
import UIKit

/// Swift implementation of the KMP `GoogleSignInProvider` ObjC protocol.
///
/// The KMP `features/authentication/domain` module exposes the `GoogleSignInProvider`
/// Kotlin interface as an ObjC protocol (visible here as `GoogleSignInProvider` after
/// `import ComposeApp`). This class bridges every call through to the GoogleSignIn-iOS
/// Swift SDK, which requires a presenting `UIViewController` only obtainable from UIKit.
///
/// Lifecycle:
///   - Create a single instance for the app lifetime (stored in `iOSApp`).
///   - `init(webClientId:)` stores the OAuth2 server client ID used by Firebase.
///   - `signIn(onSuccess:onError:)` resolves the top-most view controller at call time,
///     so there is no need to pass a UIViewController from Kotlin.
///
/// Registration:
///   This instance is passed to `MainViewControllerKt.mainViewController(googleSignInProvider:)`,
///   which includes it in the Koin graph via `googleSignInProviderModule(provider)`,
///   overriding the default `NoOpGoogleSignInProvider` registered by @ComponentScan.
final class SwiftGoogleSignInProvider: NSObject, GoogleSignInProvider {

    private let webClientId: String

    /// - Parameter webClientId: OAuth2 web client ID (server client ID used by Firebase).
    ///   Read from the KMP BuildConfig via `GoogleSignInProviderModuleKt.googleSignInWebClientId`.
    init(webClientId: String) {
        self.webClientId = webClientId
        super.init()
    }

    // MARK: - GoogleSignInProvider

    func signIn(
        onSuccess: @escaping (String, String) -> Void,
        onError: @escaping (String) -> Void
    ) {
        Task { @MainActor in
            guard let presentingVC = self.topViewController() else {
                onError("Google Sign-In: could not find a presenting view controller")
                return
            }

            guard let clientID = FirebaseApp.app()?.options.clientID else {
                onError("Google Sign-In: Firebase is not configured (missing clientID)")
                return
            }

            GIDSignIn.sharedInstance.configuration = GIDConfiguration(
                clientID: clientID,
                serverClientID: self.webClientId
            )

            do {
                let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: presentingVC)
                // Refresh tokens to ensure a fresh server auth code / ID token.
                let user = try await result.user.refreshTokensIfNeeded()
                guard let idToken = user.idToken?.tokenString else {
                    onError("Google Sign-In: no ID token in sign-in result")
                    return
                }
                let accessToken = user.accessToken.tokenString
                onSuccess(idToken, accessToken)
            } catch {
                onError(error.localizedDescription)
            }
        }
    }

    // MARK: - Private

    /// Walks the view controller hierarchy to find the topmost presented controller,
    /// which is the correct presenter for the Google Sign-In sheet.
    @MainActor
    private func topViewController() -> UIViewController? {
        guard
            let windowScene = UIApplication.shared.connectedScenes
                .compactMap({ $0 as? UIWindowScene })
                .first(where: { $0.activationState == .foregroundActive }),
            let window = windowScene.keyWindow
        else { return nil }

        var top: UIViewController? = window.rootViewController
        while let presented = top?.presentedViewController {
            top = presented
        }
        return top
    }
}
