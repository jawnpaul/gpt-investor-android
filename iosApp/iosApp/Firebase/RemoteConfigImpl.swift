//
//  RemoteConfigImpl.swift
//  iosApp
//
//  Created by ABDULKARIM ABDULRAHMAN on 19/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import GPT_Investor
import FirebaseRemoteConfig

class RemoteConfigImpl: IRemoteConfig {
    
    var remoteConfig: RemoteConfig!
    init() {
        remoteConfig = RemoteConfig.remoteConfig()
        let settings = RemoteConfigSettings()
        #if DEBUG
            settings.minimumFetchInterval = 3600
        #else
            settings.minimumFetchInterval = 0
        #endif
        remoteConfig.configSettings = settings
        remoteConfig.setDefaults(fromPlist: "RemoteConfigDefaults")
        addConfigUpdateListener()
    }
    
    private func addConfigUpdateListener() {
        remoteConfig.addOnConfigUpdateListener { (configUpdate: RemoteConfigUpdate?, error: Error?) -> Void in
            guard let configUpdate = configUpdate, error == nil else { // Added an explicit unwrap for configUpdate here for clarity if you use it this way
                print("Config update error with code: \(error?.localizedDescription ?? "Unknown error")")
                return
            }
            print("Updated keys: \(configUpdate.updatedKeys)")
        }
    }

    func fetchAndActivateStringValue(configKey: String, completionHandler: @escaping (String?, Error?) -> Void) {
        Task {
            do {
                try await remoteConfig.fetchAndActivate()
                let result = remoteConfig[configKey].stringValue ?? ""
                completionHandler(result, nil)
            } catch {
                completionHandler(nil, error)
            }
        }
    }

    func fetchAndActivateValue(configKey: String, completionHandler: @escaping (KotlinFloat?, Error?) -> Void) {
        Task {
            do {
                try await remoteConfig.fetchAndActivate()
                let result = remoteConfig[configKey].numberValue.floatValue
                completionHandler(KotlinFloat(value: result), nil) // Wrap Float in KotlinFloat
            } catch {
                completionHandler(nil, error)
            }
        }
    }

    func fetchAndActivateBooleanValue(configKey: String, completionHandler: @escaping (KotlinBoolean?, Error?) -> Void) {
        Task {
            do {
                try await remoteConfig.fetchAndActivate()
                let result = remoteConfig[configKey].boolValue
                completionHandler(KotlinBoolean(value: result), nil) // Wrap Bool in KotlinBoolean
            } catch {
                completionHandler(nil, error)
            }
        }
    }
}
