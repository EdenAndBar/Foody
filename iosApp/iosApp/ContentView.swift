import SwiftUI
import Shared
import CoreLocation

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()
    @StateObject private var locationManager = LocationManager()
    @State private var hasLoadedRestaurants = false

    var body: some View {
        NavigationStack(path: $path) {
            VStack {
                HStack {
                    Spacer()
                    if hasLoadedRestaurants && locationManager.location != nil {
                        Button(action: {
                            locationManager.refreshLocation()
                        }) {
                            Label("Refresh location", systemImage: "location.circle")
                        }
                        .padding(.trailing)
                    }
                }
                
                if restaurants.isEmpty {
                    ProgressView("Looking for restaurants near you...")
                } else {
                    RestaurantListView(
                        restaurants: restaurants,
                        favorites: $favorites,
                        onTap: { restaurant in
                            path.append(restaurant)
                        }
                    )
                }
            }
            .navigationDestination(for: Restaurant.self) { restaurant in
                BottomSheetView(restaurant: restaurant, favorites: $favorites)
            }
        }
        .onReceive(locationManager.$location.compactMap { $0 }) { coordinate in
            print("📍 Got location update: \(coordinate.latitude), \(coordinate.longitude)")
            let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
            loadNearbyRestaurants(location: latLng)
        }
        .onAppear {
            if locationManager.location == nil {
                locationManager.refreshLocation()
            }
        }
    }

    private func loadNearbyRestaurants(location: String) {
        print("🍽 Loading restaurants for location: \(location)")
        let api = RestaurantApi()
        api.getRestaurants(location: location) { results in
            print("✅ Received \(results.count) restaurants")
            self.restaurants = results
            self.hasLoadedRestaurants = true
        }
    }

}


