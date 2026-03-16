// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "iosApp",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .executable(name: "iosApp", targets: ["iosApp"])
    ],
    dependencies: [
        .package(
            url: "https://github.com/firebase/firebase-ios-sdk",
            from: "12.10.0"
        )
    ],
    targets: [
        .executableTarget(
            name: "iosApp",
            dependencies: [
                "ComposeApp",
                .product(name: "FirebaseAnalytics", package: "firebase-ios-sdk"),
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseCore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseMessaging", package: "firebase-ios-sdk"),
                .product(name: "FirebaseRemoteConfig", package: "firebase-ios-sdk"),
            ],
            path: "/iosApp"
        ),
        .binaryTarget(
            name: "ComposeApp",
            path: "../composeApp/build/XCFrameworks/release/ComposeApp.xcframework"
        )
    ]
)