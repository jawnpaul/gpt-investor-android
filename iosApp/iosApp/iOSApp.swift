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
        Task{
            await startKoin()
        }
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
    
    private func startKoin() async {
        let mixPanelLogger = MixpanelLogger() as AnalyticsLogger
        let firebaseLogger = FirebaseLogger() as AnalyticsLogger
        let firebaseRemoteConfig = RemoteConfigImpl() as IRemoteConfig
        let geminiApi = (await GeminiApiImpl(remoteConfig: firebaseRemoteConfig)) as GeminiApi
        let mixPanelModule = AnalyticsProvider_iosKt.providesMixpanelLogger(mixpanel: mixPanelLogger)
        let firebaseModule = AnalyticsProvider_iosKt.providesFirebaseLogger(firebase: firebaseLogger)
        let remoteConfigModule = RemoteConfigProvider_iosKt.providesRemoteConfig(config: firebaseRemoteConfig)
        let geminiApiModule = GeminiApiProvider_iosKt.providesGeminiApi(api: geminiApi)
        KoinInitializerKt.doInitKoin(config: nil, platformModules: [mixPanelModule, firebaseModule, remoteConfigModule, geminiApiModule])
    }
}
