//import SwiftUI
//import FirebaseAuth
//
//struct MainTabView: View {
//    @State private var favorites: [Restaurant] = []
//    @Binding var isLoggedIn: Bool
//
//    var body: some View {
//        TabView {
//            ContentView(favorites: $favorites)
//                .tabItem {
//                    Image(systemName: "house.fill")
//                    Text("Main")
//                }
//
//            FavoritesView(favorites: $favorites)
//                .tabItem {
//                    Image(systemName: "heart.fill")
//                    Text("Favorites")
//                }
//
//            LocationSearchView(favorites: $favorites)
//                .tabItem {
//                    Image(systemName: "mappin.and.ellipse")
//                    Text("Location")
//                }
//
//            CategoryView()
//                .tabItem {
//                    Image(systemName: "line.3.horizontal")
//                    Text("Category")
//                }
//
//            SettingsView(isLoggedIn: $isLoggedIn)
//                .tabItem {
//                    Image(systemName: "gear")
//                    Text("Settings")
//                }
//        }
//    }
//}
//
//struct CategoryView: View {
//    var body: some View {
//        Text("Category screen coming soon")
//    }
//}

//
//import SwiftUI
//import FirebaseAuth
//
//struct MainTabView: View {
//    @State private var favorites: [Restaurant] = []
//    @Binding var isLoggedIn: Bool
//    @State private var showLogin = false
//
//    var body: some View {
//        TabView {
//            NavigationView {
//                        VStack(spacing: 40) {
//                            if isLoggedIn {
//                                Text("Welcome to Foody!")
//                                    .font(.title2)
//                                    .bold()
//
//                                Button(action: {
//                                    try? Auth.auth().signOut()
//                                    isLoggedIn = false
//                                }) {
//                                    Text("Sign Out")
//                                        .frame(maxWidth: .infinity)
//                                        .padding()
//                                        .background(Color.red)
//                                        .foregroundColor(.white)
//                                        .cornerRadius(12)
//                                        .padding(.horizontal)
//                                }
//
//                            } else {
//                                Text("You are not logged in.")
//                                    .font(.title3)
//                                    .foregroundColor(.gray)
//
//                                Button(action: {
//                                    showLogin = true
//                                }) {
//                                    HStack {
//                                        Image(systemName: "person.crop.circle.badge.plus")
//                                        Text("Sign In / Sign Up")
//                                            .bold()
//                                    }
//                                    .frame(maxWidth: .infinity)
//                                    .padding()
//                                    .background(Color.blue)
//                                    .foregroundColor(.white)
//                                    .cornerRadius(12)
//                                    .padding(.horizontal)
//                                }
//                            }
//                        }
//                        .navigationTitle("Foody App")
//                        .sheet(isPresented: $showLogin) {
//                            LoginView(isLoggedIn: $isLoggedIn) {
//                                isLoggedIn = true
//                                showLogin = false
//                            }
//                        }
//                    }
//            .tabItem {
//                Image(systemName: "house.fill")
//                Text("Main")
//            }
//
//            FavoritesView(favorites: $favorites)
//                .tabItem {
//                    Image(systemName: "heart.fill")
//                    Text("Favorites")
//                }
//
//            LocationSearchView(favorites: $favorites)
//                .tabItem {
//                    Image(systemName: "mappin.and.ellipse")
//                    Text("Location")
//                }
//
//            CategoryView()
//                .tabItem {
//                    Image(systemName: "line.3.horizontal")
//                    Text("Category")
//                }
//        }
//    }
//}
//
//struct CategoryView: View {
//    var body: some View {
//        Text("Category screen coming soon")
//    }
//}
//
//import SwiftUI
//import FirebaseAuth
//
//struct MainTabView: View {
//    @Binding var isLoggedIn: Bool
//    @State private var showLogin = false
//    @Binding var favorites: [Restaurant] // אם צריך להעביר לפייבוריטים
//
//    var body: some View {
//        NavigationView {
//            if isLoggedIn {
//                // 👉 זה המסך שלך עם המסעדות לפי מיקום
//                ContentView(favorites: $favorites)
//
//            } else {
//                VStack(spacing: 40) {
//                    Text("Welcome to Foody")
//                        .font(.title)
//                        .bold()
//
//                    Button(action: {
//                        showLogin = true
//                    }) {
//                        HStack {
//                            Image(systemName: "person.crop.circle.badge.plus")
//                            Text("Sign In / Sign Up")
//                                .bold()
//                        }
//                        .frame(maxWidth: .infinity)
//                        .padding()
//                        .background(Color.blue)
//                        .foregroundColor(.white)
//                        .cornerRadius(12)
//                        .padding(.horizontal)
//                    }
//                }
//                .navigationTitle("Foody App")
//                .sheet(isPresented: $showLogin) {
//                    LoginView(isLoggedIn: $isLoggedIn) {
//                        isLoggedIn = true
//                        showLogin = false
//                    }
//                }
//            }
//        }
//    }
//}
//

import SwiftUI
import FirebaseAuth

struct MainTabView: View {
    @Binding var isLoggedIn: Bool
    @Binding var favorites: [Restaurant]

    var body: some View {
        TabView {
            ContentView(favorites: $favorites, isLoggedIn: $isLoggedIn)
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Home")
                }

            FavoritesView(favorites: $favorites)
                .tabItem {
                    Image(systemName: "heart.fill")
                    Text("Favorites")
                }

            LocationSearchView(favorites: $favorites)
                .tabItem {
                    Image(systemName: "mappin.and.ellipse")
                    Text("Location")
                }

            CategoryView()
                .tabItem {
                    Image(systemName: "line.3.horizontal")
                    Text("Category")
                }
        }
    }
}

struct CategoryView: View {
    var body: some View {
        Text("Category screen coming soon")
    }
}

