//
//  UserImpl.swift
//  iosApp
//
//  Created by ABDULKARIM ABDULRAHMAN on 11/09/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
import GPT_Investor
import FirebaseAuth

class UserImpl: IUser {
    var user: FirebaseAuth.User!
    
    init(user: FirebaseAuth.User!) {
        self.user = user
    }
    
    func delete(completionHandler: @escaping ((any Error)?) -> Void) {
        self.delete {_ in
            completionHandler(nil)
        }
    }
    
    var displayName: String? {
        return user.displayName
    }
    
    var email: String? {
        return user.email
    }
    
    var providerId: String {
        return user.providerID
    }

    var uid: String {
        return user.uid
    }
}

extension User {
    func toUser() -> IUser {
        UserImpl(user: self)
    }
}
