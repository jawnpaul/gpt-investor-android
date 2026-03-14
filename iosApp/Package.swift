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
    targets: [
        .executableTarget(
            name: "iosApp",
            dependencies: ["ComposeApp"],
            path: "Sources/iosApp"
        ),
        .binaryTarget(
            name: "ComposeApp",
            path: "../composeApp/build/XCFrameworks/release/ComposeApp.xcframework"
        )
    ]
)
