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
        ),
        .package(
            url: "https://github.com/youtube/youtube-ios-player-helper",
            from: "1.0.4"
        ),
        .package(
            url: "https://github.com/mixpanel/mixpanel-swift",
            from: "5.2.0"
        ),
        .package(
            url: "https://github.com/google/GoogleSignIn-iOS",
            from: "8.0.0"
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
                .product(name: "YouTubeiOSPlayerHelper", package: "youtube-ios-player-helper"),
                .product(name: "Mixpanel", package: "mixpanel-swift"),
                .product(name: "GoogleSignIn", package: "GoogleSignIn-iOS"),
            ],
            path: "/iosApp"
        ),
        .binaryTarget(
            name: "ComposeApp",
            path: "../composeApp/build/XCFrameworks/release/ComposeApp.xcframework"
        )
    ]
)