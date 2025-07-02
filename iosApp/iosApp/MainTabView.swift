import SwiftUI
import FirebaseAuth

struct MainTabView: View {
    @Binding var isLoggedIn: Bool
    @Binding var favorites: [Restaurant]
    @State private var restaurants: [Restaurant] = []
    @State private var didLoad = false

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

            MapView()
                .tabItem {
                        Label("Map", systemImage: "map")
                    }
        }
        .onAppear {
                    if !didLoad {
                        didLoad = true
                        let api = RestaurantApi()
                        api.getRestaurants(city: "tel aviv") { result in
                            self.restaurants = result
                        }
                    }
                }
    }
}

struct MapView: View {
    var body: some View {
        Text("Map View Placeholder")
    }
}
