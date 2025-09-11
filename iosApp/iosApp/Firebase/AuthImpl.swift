//
//  FAuthImpl.swift
//  iosApp
//
//  Created by ABDULKARIM ABDULRAHMAN on 11/09/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import GPT_Investor
import FirebaseAuth
import SwiftUI

class AuthImpl: IFirebaseAuth {
    var auth: FirebaseAuth.Auth!
    
    init() {
        auth = FirebaseAuth.Auth.auth()
    }
    
    func createUserWithEmailAndPassword(email: String, password: String, completionHandler: @escaping (Any?, (any Error)?) -> Void) {
        <#code#>
    }
    
    func getAuthenticationState() -> any Kotlinx_coroutines_coreFlow {
        let bridger = SwiftFlow<KotlinBoolean>()
        let handler = auth.addStateDidChangeListener { user, error in
            bridger.emit(value: KotlinBoolean(value: user.currentUser != nil))
        }
        return bridger.asFlow()
    }
    
    func signInWithCredentials(completionHandler: @escaping (Any?, (any Error)?) -> Void) {
        <#code#>
    }
    
    
    func signInWithEmailAndPassword(email: String, password: String, completionHandler: @escaping (Any?, (any Error)?) -> Void) {
        var result: Result<IUser?, any Error> = .failure(Error(string: "User not found"))
        auth.signIn(withEmail: email, password: password) { authResult, error in
            guard let user = authResult?.user, error == nil else {
                result = .failure(error!)
                completionHandler(nil, error)
            }
            result = .success(user.toUser())
        }
        completionHandler(result, nil)
    }
    
    func signOut(completionHandler: @escaping ((any Error)?) -> Void) {
        Task {
            do {
                try auth.signOut()
                completionHandler(nil)
            } catch {
                completionHandler(error)
            }
        }
    }
    
    var currentUser: (IUser)? {
        return auth.currentUser?.toUser()
    }

    
    actual suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        auth.createUserWithEmail(email = email, password = password) { authResult, error ->
            val authData = authResult?.user()
            result = when {
                error != null -> Result.failure(error.toException())
                authData != null -> Result.success(authData.toUser())
                else -> Result.failure(Exception("Unknown error"))
                    }
        }
        return result
    }
    
    actual suspend fun signInWithCredentials(): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        val clientID = FIRApp.defaultApp()?.options?.clientID
        ?: return Result.failure(Exception("Client ID not found"))
        val config = GIDConfiguration(clientID = clientID)
        GIDSignIn.sharedInstance.configuration = config
        val viewController = ((UIApplication.sharedApplication.connectedScenes.first()
                               as? UIWindowScene)?.windows()?.first() as? UIWindow)?.rootViewController
        ?: return Result.failure(Exception("No root view controller found"))
        GIDSignIn
            .sharedInstance
            .signInWithPresentingViewController(
                presentingViewController = viewController
            ) { authResult, error ->
                {
                    result = when {
                        error != null -> Result.failure(error.toException())
                        authResult != null -> {
                            val idToken = authResult.user.idToken()?.tokenString()
                            val accessToken = authResult.user.accessToken.tokenString()
                            if (idToken != null) {
                                val credential = FIRGoogleAuthProvider.credentialWithIDToken(
                                    idToken = idToken,
                                    accessToken = accessToken
                                )
                                auth.signInWithCredential(credential) { signInResult, signInError ->
                                    result = when {
                                        signInError != null -> {
                                            Result.failure(signInError.toException())
                                        }
                                        signInResult?.user() != null -> {
                                            Result.success(signInResult.user().toUser())
                                        }
                                        
                                        else -> {
                                            Result.failure(
                                                Exception("Unknown error during sign-in")
                                            )
                                        }
                                    }
                                }
                                result
                            } else {
                                Result.failure(Exception("Failed to retrieve tokens"))
                            }
                        }
                        
                        else -> Result.failure(Exception("Unknown error"))
                            }
                    
                }
            }
        return result
    }
    
    actual suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User?> {
        var result: Result<User?> = Result.failure(Exception("User not found"))
        auth.signInWithEmail(email = email, password = password) { authResult, error ->
            val authData = authResult?.user()
            result = when {
                error != null -> Result.failure(error.toException())
                authData != null -> Result.success(authData.toUser())
                else -> Result.failure(Exception("Unknown error"))
                    }
        }
        return result
    }
}
