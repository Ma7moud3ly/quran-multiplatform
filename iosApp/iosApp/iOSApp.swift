import SwiftUI

@main
struct iOSApp: App {
    init() {
        KoinIOSKt.doInitKoinForIOS()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}