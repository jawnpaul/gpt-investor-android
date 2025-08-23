//
//  MixpanelLogger.swift
//  iosApp
//
//  Created by ABDULKARIM ABDULRAHMAN on 05/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Swift
import GPT_Investor
import Mixpanel

class MixpanelLogger: AnalyticsLogger {
    var api: MixpanelInstance!
    init() {
        guard let value = Config.getConfigValue(for: "MixpanelKey") else {
            print("Mixpanel token not found in Info.plist")
            return
        }
        Mixpanel.initialize(token: value, flushInterval: 60, optOutTrackingByDefault: true)
        api = Mixpanel.mainInstance()
    }
    func identifyUser(eventName: String, params: [String: Any]) {
        var props: [String: MixpanelType] = [:]
        params.forEach{(key, value) in
            if key != "user_id" {
                props[key] = value as? any MixpanelType
            }
        }
        if let userId = params["user_id"] as? String {
            api.identify(distinctId: userId, usePeople: true)
            api.people.set(properties: props)
        } else {
            print("User ID not found in params")
        }
    }

    func logEvent(eventName: String, params: [String: Any]) {
        // compactMapValues will iterate through the dictionary's values.
        // For each value, it attempts the cast to MixpanelType.
        // If the cast succeeds, the key-value pair is kept with the casted value.
        // If the cast returns nil (i.e., it's not a MixpanelType), the key-value pair is removed.
        let mixpanelProperties = params.compactMapValues { $0 as? MixpanelType }

        if !mixpanelProperties.isEmpty || params.isEmpty {
            api.track(event: eventName, properties: mixpanelProperties)
        } else if !params.isEmpty && mixpanelProperties.isEmpty {
            print("Warning: All properties for event '\(eventName)' were of incompatible types. Tracking event without properties.")
            api.track(event: eventName)
        }
    }

    func logViewEvent(screenName: String) {

    }

    func resetUser(eventName: String) {
        api.track(event: eventName)
        api.reset()
    }
}
