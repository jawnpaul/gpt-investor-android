//
// Created by ABDULKARIM ABDULRAHMAN on 08/07/2025.
// Copyright (c) 2025 orgName. All rights reserved.
//

import Foundation

struct Config {

    static func getConfigValue(for key: String) -> String? {
        // For debugging: Print all available keys in the Info.plist
        // print("Available keys in Info.plist: \(Bundle.main.infoDictionary?.keys ?? [:])")

        guard let infoDict = Bundle.main.infoDictionary else {
            print("Config.swift: Error - Info.plist dictionary not found.")
            return nil
        }

        let value = infoDict[key]

        if value == nil {
            print("Config.swift: Key '\(key)' not found in Info.plist. Available keys are: \(infoDict.keys)")
            return nil
        }

        guard let stringValue = value as? String else {
            print("Config.swift: Value for key '\(key)' was found, but it is not a String. It is of type: \(type(of: value!)). Consider checking its type in Info.plist.")
            return nil
        }
        return stringValue
    }

    static func getFilePathName(for key: String, path pathKey: String? = nil) -> String? {
        guard let fileName = getConfigValue(for: key) else {
            print("Config.swift: File name for key '\(key)' not found.")
            return nil
        }

        let filePath = Bundle.main.path(forResource: fileName, ofType: pathKey)

        if filePath == nil {
            print("Config.swift: File path for '\(fileName)' not found in the main bundle.")
            return nil
        } else {
            print("Config.swift: File path for '\(fileName)' found at: \(filePath!)")
        }

        return filePath
    }
}
