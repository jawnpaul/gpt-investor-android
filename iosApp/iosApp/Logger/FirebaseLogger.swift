//
//  FirebaseLogger.swift
//  iosApp
//
//  Created by ABDULKARIM ABDULRAHMAN on 13/07/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Swift
import GPT_Investor
import FirebaseAnalytics

class FirebaseLogger: AnalyticsLogger {

    func identifyUser(eventName: String, params: [String: Any]) {
        guard let userId = params["user_id"] as? String else {
            print("User ID not found in params")
            return
        }
        Analytics.setUserID(userId)
        params.forEach { (key, value) in
            if key != "user_id" {
                Analytics.setUserProperty(value as? String, forName: key)
            }
        }
    }

    func logEvent(eventName: String, params: [String: Any]) {
        Analytics.logEvent(eventName, parameters: params)
    }

    func logViewEvent(screenName: String) {

    }

    func resetUser(eventName: String) {
        Analytics.resetAnalyticsData()
    }
}

