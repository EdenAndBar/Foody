import SwiftUI
import Shared

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            VStack {
                if restaurants.isEmpty {
                    ProgressView("Loading restaurants...")
                        .onAppear { loadRestaurants() }
                } else {
                    RestaurantListView(
                        restaurants: restaurants,
                        favorites: $favorites,
                        onTap: { restaurant in
                            path.append(restaurant.placeId)
                        }
                    )
                }
            }
            // ✅ שימי לב שזה חלק מתוך NavigationStack – לא מחוץ לו!
            .navigationDestination(for: String.self) { placeId in
                if let restaurant = restaurants.first(where: { $0.placeId == placeId }) {
                    BottomSheetView(
                        restaurant: restaurant,
                        favorites: $favorites
                    )
                } else {
                    Text("Restaurant not found")
                }
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
