//import SwiftUI
//import FirebaseAuth
//
//@main
//struct iOSApp: App {
//    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
//    @State private var isLoggedIn = false
//    @State private var favorites: [Restaurant] = []
//
//    var body: some Scene {
//        WindowGroup {
//            MainTabView(isLoggedIn: $isLoggedIn, favorites: $favorites)
//                .onAppear {
//                    isLoggedIn = Auth.auth().currentUser != nil
//                }
//        }
//    }
//}

import SwiftUI
import FirebaseAuth

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @State private var isLoggedIn = false
    @State private var favorites: [Restaurant] = []

    var body: some Scene {
        WindowGroup {
            if isLoggedIn {
                MainTabView(isLoggedIn: $isLoggedIn, favorites: $favorites)
            } else {
                LoginView(isLoggedIn: $isLoggedIn) {
                    isLoggedIn = true
                }
            }
        }
    }
}

