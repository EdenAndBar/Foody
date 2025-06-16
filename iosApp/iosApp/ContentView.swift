import SwiftUI
import Shared

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var searchText = ""
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var showingSheet = false

    var body: some View {
        NavigationView {
            if restaurants.isEmpty {
                ProgressView("Loading restaurants...")
                    .onAppear { loadRestaurants() }
            } else {
                RestaurantListView(
                    restaurants: restaurants,
                    favorites: $favorites,
                    onTap: { restaurant in
                        selectedRestaurant = restaurant
                        showingSheet = true
                    }
                )
            }
        }
        .sheet(isPresented: $showingSheet) {
            if let restaurant = selectedRestaurant {
                BottomSheetView(
                    restaurant: restaurant,
                    favorites: $favorites
                )
            }
        }
    }

    private func loadRestaurants() {
        let api = RestaurantApi()
        api.getRestaurants { results in
            self.restaurants = results
        }
    }
}
