import AuthenticationServices
import CryptoKit
import Foundation
//
// Created by ABDULKARIM ABDULRAHMAN on 23/04/2026.
//
import UIKit

@objc public class AppleAuthProvider: NSObject {
    
    @objc public static let shared = AppleAuthProvider()
    private var currentOnSuccess: ((String, String) -> Void)?
    private var currentOnError: ((String) -> Void)?
    private var currentNonce: String?
    
    @available(iOS 13, *)
    @objc public func signInWithApple(
        _ onSuccess: @escaping (String, String) -> Void,
        onError error: @escaping (String) -> Void
    ) {
        self.currentNonce = randomNonceString()
        self.currentOnSuccess = onSuccess
        self.currentOnError = error
        
        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        if let currentNonce {
            request.nonce = sha256(currentNonce)
        }
        
        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = self
        authorizationController.presentationContextProvider = self
        authorizationController.performRequests()
    }
    
    @available(iOS 13, *)
    private func sha256(_ input: String) -> String {
        let inputData = Data(input.utf8)
        let hashedData = SHA256.hash(data: inputData)
        let hashString = hashedData.compactMap {
            String(format: "%02x", $0)
        }.joined()
        return hashString
    }
    
    private func randomNonceString(length: Int = 32) -> String {
        precondition(length > 0)
        var randomBytes = [UInt8](repeating: 0, count: length)
        let errorCode = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)
        if errorCode != errSecSuccess {
            fatalError("Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)")
        }
        
        let charset: [Character] = Array("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._")
        let nonce = randomBytes.map { byte in
            charset[Int(byte) % charset.count]
        }
        return String(nonce)
    }
}

@available(iOS 13, *)
extension AppleAuthProvider: ASAuthorizationControllerDelegate {
    public func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization authorization: ASAuthorization
    ) {
        guard
            let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential,
            let identityTokenData = appleIDCredential.identityToken,
            let tokenString = String(data: identityTokenData, encoding: .utf8),
            let nonce = currentNonce
        else {
            currentOnError?("Unable to parse Apple ID crendentials.")
            return
        }
        
        currentOnSuccess?(tokenString, nonce)
    }
    
    public func authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError error: Error
    ) {
        currentOnError?(error.localizedDescription)
    }
}

@available(iOS 13.0, *)
extension AppleAuthProvider: ASAuthorizationControllerPresentationContextProviding {
    @objc public func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        // Prefer an active foreground UIWindowScene
        let scenes = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
        // Try foreground active first
        if let activeScene = scenes.first(where: { $0.activationState == .foregroundActive }),
           let keyWindow = activeScene.windows.first(where: { $0.isKeyWindow })
        {
            return keyWindow
        }
        // Fallback: any scene's key window
        for scene in scenes {
            if let keyWindow = scene.windows.first(where: { $0.isKeyWindow }) {
                return keyWindow
            }
        }
        // As a last resort, return any window from any scene
        if let anyWindow = scenes.first?.windows.first {
            return anyWindow
        }
        // Create a temporary window to provide an anchor if nothing else is available
        let tempWindow = UIWindow(frame: UIScreen.main.bounds)
        tempWindow.makeKeyAndVisible()
        return tempWindow
    }
}
