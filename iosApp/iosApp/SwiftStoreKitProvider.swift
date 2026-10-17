//
// Created by ABDULKARIM ABDULRAHMAN on 17/10/2026.
//

import Foundation
import StoreKit
import ComposeApp

@MainActor
final class SwiftStoreKitProvider: NSObject, StoreKitProvider {

    // MARK: - StoreKitProvider
    func purchase(
        productId: String,
        onSuccess: @escaping (String, String, String) -> Void,
        onError: @escaping (String) -> Void,
        onCancelled: @escaping () -> Void
    ) {
        Task {
            do {
                let products = try await Product.products(for: [productId])
                guard let product = products.first else {
                    onError("Product not found: \(productId)")
                    return
                }

                let result = try await product.purchase()

                switch result {
                case .success(let verification):
                    switch verification {
                    case .verified(let transaction):
                        let purchaseToken = verification.jwsRepresentation
                        let orderId = String(transaction.id)

                        await transaction.finish()
                        onSuccess(purchaseToken, productId, orderId)
                    case .unverified(_, let error):
                        onError("Purchase verification failed: \(error.localizedDescription)")
                    }
                case .userCancelled:
                    onCancelled()
                case .pending:
                    onError("Purchase is pending parental approval")
                @unknown default:
                    onError("Unknown purchase error")
                }
            } catch {
                onError(error.localizedDescription)
            }
        }
    }

    func fetchCurrentEntitlements(
        onResult: @escaping ([String], [String]) -> Void
    ) {
        Task {
            var tokens: [String] = []
            var productIds: [String] = []

            for await verification in Transaction.currentEntitlements {
                if case .verified(let transaction) = verification {
                    tokens.append(verification.jwsRepresentation)
                    productIds.append(String(transaction.productID))
                }
            }

            onResult(tokens, productIds)
        }
    }
}