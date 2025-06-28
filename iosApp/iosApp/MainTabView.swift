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

