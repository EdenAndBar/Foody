import SwiftUI

struct MainTabView: View {
    @State private var favorites: [Restaurant] = []

    var body: some View {
        TabView {
            ContentView(favorites: $favorites)
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Main")
                }

            FavoritesView(favorites: $favorites)
                            .tabItem {
                                Image(systemName: "heart.fill")
                                Text("Favorites")
                            }
            LocationView()
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

struct LocationView: View {
    var body: some View {
        Text("Location screen coming soon")
    }
}

struct CategoryView: View {
    var body: some View {
        Text("Category screen coming soon")
    }
}
