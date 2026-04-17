//
// Created by Александр Лебедь on 12.02.2026.
//

import Foundation
import SwiftUI
import Shared
import UIKit
import UserNotifications

class AppDelegate: NSObject, UIApplicationDelegate {

    let backDispatcher = BackDispatcherKt.BackDispatcher()
    lazy var root: ComponentContext = DefaultComponentContext(
        lifecycle: ApplicationLifecycle(),
        stateKeeper: nil,
        instanceKeeper: nil,
        backHandler: backDispatcher,
    )

    let rootViewController: UIViewController = UIViewController()
    lazy var appGraph: IosAppGraph = {
        let platform = Platform_iosKt.getPlatform(
                viewController: rootViewController,
                flavors: IosAppFlavors(isDebug: isDebug)
        )
        return IosAppGraphKt.createRootGraph(platform: platform, environment: MyNetworkEnvironment.Prod())
    }()

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        appGraph.appDelegate.initialize()
        L.shared.i(message: "App started")
        return true
    }

    var isDebug: Bool {
        #if DEBUG
        return true
        #else
        return false
        #endif
    }
}
