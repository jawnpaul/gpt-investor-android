import SwiftUI
import FirebaseCore
import GPT_Investor

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        print("AppDelegate: didFinishLaunchingWithOptions")
        startFirebase()
        startKoin()
        return true
    }
    
    private func startFirebase() {
        // Move Firebase configuration here
        if let filePath = Config.getFilePathName(for: "GoogleServices Info", path: "plist") {
            if let options = FirebaseOptions(contentsOfFile: filePath) {
                FirebaseApp.configure(options: options)
                print("Firebase configured successfully from AppDelegate.")
            } else {
                print("Error: Could not initialize FirebaseOptions from file: \(filePath)")
            }
        } else {
            print("Error: GoogleServices-Info.plist file path not found.")
        }
    }
    
    private func startKoin() {
        let mixPanelLogger = MixpanelLogger() as AnalyticsLogger
        let firebaseLogger = FirebaseLogger() as AnalyticsLogger
        let mixPanelModule = AnalyticsProvider_iosKt.providesMixpanelLogger(mixpanel: mixPanelLogger)
        let firebaseModule = AnalyticsProvider_iosKt.providesFirebaseLogger(firebase: firebaseLogger)
        KoinInitializerKt.doInitKoin(config: nil, platformModules: [mixPanelModule, firebaseModule])
    }
}
