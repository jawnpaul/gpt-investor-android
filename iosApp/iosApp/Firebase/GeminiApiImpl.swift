
//
//  GeminiApiImpl.swift
//  iosApp
//
//  Created by ABDULKARIM ABDULRAHMAN on 22/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import GPT_Investor
import FirebaseAI
import SwiftUI

class GeminiApiImpl: NSObject, GeminiApi {
    
    var remoteConfig: IRemoteConfig
    var model: GenerativeModel!
    var modelName: String!
    let constants = Constants.init()
    
    init(remoteConfig: IRemoteConfig) async {
        self.remoteConfig = remoteConfig
        do {
            self.modelName = try await remoteConfig.fetchAndActivateStringValue(configKey: constants.MODEL_NAME_KEY)
            print("Gemini model initialized with name: \(String(describing: modelName))")
        } catch {
            print("Error fetching Gemini model name: \(error.localizedDescription)")
        }
    }
    
    func sendMessage(prompt: String, history: [History], completionHandler: @escaping (String?, (any Error)?) -> Void) {
        let chat = startChat(of: history)
        Task {
            do {
                let response = try await chat.sendMessage(prompt)
                completionHandler(response.text, nil)
            } catch {
                print("Error sending message: \(error.localizedDescription)")
                completionHandler(nil, error)
            }
        }
    }
    
    func sendMessageStream(prompt: String, history: [History]) -> any Kotlinx_coroutines_coreFlow {
        let bridger = SwiftFlow<NSString>()
        let chat = startChat(of: history)
        Task {
            do {
                let stream = try chat.sendMessageStream(prompt)
                for try await value in stream {
                    bridger.emit(value: value.text as? NSString)
                }
                bridger.complete()
            } catch {
                bridger.error(throwable: asKotlinThrowable(error))
            }
        }
        return bridger.asFlow()
    }
    
    private func startChat(of history: [History]) -> Chat {
        initializeApi()
        return model.startChat(history: history.map { $0.toModelContent() })
    }
    
    private func initializeApi() {
        guard let modelName = self.modelName else {
            print("Error: Model name is not available for API initialization.")
            return
        }
        let ai = FirebaseAI.firebaseAI(backend: .googleAI())
        let config = GenerationConfig(
            temperature: constants.TEMPERATURE_VALUE,
            topP: constants.TOP_P_VALUE,
            topK: Int(constants.TOP_K_VALUE),
            maxOutputTokens: Int(constants.MAX_OUTPUT_TOKENS)
        )
        let safetySettings = [
            SafetySetting(harmCategory: .harassment, threshold: .blockMediumAndAbove),
            SafetySetting(harmCategory: .hateSpeech, threshold: .blockMediumAndAbove),
            SafetySetting(harmCategory: .sexuallyExplicit, threshold: .blockMediumAndAbove),
            SafetySetting(harmCategory: .dangerousContent, threshold: .blockMediumAndAbove)
        ]
        let systemInstruction = ModelContent(parts: constants.SYSTEM_INSTRUCTIONS)
        self.model = ai.generativeModel(
            modelName: modelName,
            generationConfig: config,
            safetySettings: safetySettings,
            systemInstruction: systemInstruction
        )
    }
    
    private func asKotlinThrowable(_ error: Error) -> KotlinThrowable {
        return KotlinThrowable(message: error.localizedDescription)
    }
    
}

extension  History {
    func toModelContent() -> ModelContent {
        return ModelContent(role: self.role, parts: self.text)
    }
}
