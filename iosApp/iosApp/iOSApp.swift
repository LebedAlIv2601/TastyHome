import SwiftUI
import Shared


@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate

    var body: some Scene {
        WindowGroup {
            MainView(appDelegate: appDelegate)
        }
    }
}
