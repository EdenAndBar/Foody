import SwiftUI
import FirebaseAuth

struct MainTabView: View {
    @Binding var isLoggedIn: Bool
    @Binding var favorites: [Restaurant]
    @State private var restaurants: [Restaurant] = []
    @State private var didLoad = false
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var selectedTab = 0
    @State private var previousTab = 0
    @EnvironmentObject var session: UserSession

    var body: some View {
        TabView(selection: $selectedTab) {
            ContentView(
                favorites: $favorites,
                isLoggedIn: $isLoggedIn,
                filter: RestaurantFilter()
            )
            .tabItem {
                Image(systemName: "house.fill")
                Text("Home")
            }

            FavoritesView(
                favorites: $favorites,
                filter: RestaurantFilter()
            )
            .tabItem {
                Image(systemName: "heart.fill")
                Text("Favorites")
            }

            LocationSearchView(
                favorites: $favorites,
                //filter: RestaurantFilter()
            )
            .tabItem {
                Image(systemName: "mappin.and.ellipse")
                Text("Location")
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
