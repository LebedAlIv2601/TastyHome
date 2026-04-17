//
//  ContentView.swift
//  iosApp
//
//  Created by Александр Лебедь on 17.04.2026.
//

import SwiftUI
import Shared

final class RootComponentHolder {
    let appGraph: IosAppGraph
    let root: ComponentContext
    lazy var rootComponent: RootComponent = {
        appGraph.rootComponentFactory().create(componentContext: root)
    }()

    init(appGraph: IosAppGraph, root: ComponentContext) {
        self.appGraph = appGraph
        self.root = root
    }
}

struct MainView: View {
    let appDelegate: AppDelegate
    @State private var rootHolder: RootComponentHolder

    init(appDelegate: AppDelegate) {
        self.appDelegate = appDelegate
        _rootHolder = State(initialValue: RootComponentHolder(appGraph: appDelegate.appGraph, root: appDelegate.root))
    }

    var body: some View {
        ContentView(appDelegate: appDelegate, rootHolder: rootHolder)
            .ignoresSafeArea(.all)
            .ignoresSafeArea(.keyboard)
            .onOpenURL { url in
                RootDeeplinkKt.handleDeeplinkUrl(rootComponent: rootHolder.rootComponent, uriString: url.absoluteString)
            }
    }
}

struct ContentView: UIViewControllerRepresentable {
    let appDelegate: AppDelegate
    let rootHolder: RootComponentHolder

    func makeUIViewController(context: Context) -> UIViewController {
        let root = appDelegate.rootViewController
        let child = RootControllerKt.rootController(
            rootComponent: rootHolder.rootComponent,
            backDispatcher: appDelegate.backDispatcher
        )
        root.addChild(child)
        root.view.addSubview(child.view)
        child.view.frame = root.view.bounds
        child.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        child.didMove(toParent: root)
        return root
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}