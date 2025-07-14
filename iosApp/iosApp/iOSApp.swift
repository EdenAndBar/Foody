import SwiftUI
import FirebaseAuth

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @State private var isLoggedIn = false
    @State private var favorites: [Restaurant] = []
    @StateObject private var session = UserSession()

    var body: some Scene {
        WindowGroup {
            if isLoggedIn {
                MainTabView(isLoggedIn: $isLoggedIn, favorites: $favorites)
                    .environmentObject(session)
            } else {
                LoginView(isLoggedIn: $isLoggedIn) {
                    isLoggedIn = true
                }
                .environmentObject(session)
            }
        }
    }
}

