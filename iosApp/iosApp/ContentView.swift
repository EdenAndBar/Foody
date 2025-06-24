import SwiftUI
import Shared
import CoreLocation
import Foundation

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()
    @StateObject private var locationManager = LocationManager()
    @State private var hasLoadedRestaurants = false
    @State private var searchText = ""

    var isSearchMode: Bool {
        !searchText.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        NavigationStack(path: $path) {
            VStack {
                HStack {
                    Spacer()
                    if hasLoadedRestaurants && restaurants.count > 0 && locationManager.location != nil && !isSearchMode {
                        Button(action: {
                            locationManager.refreshLocation()
                        }) {
                            Label("Refresh location", systemImage: "location.circle")
                        }
                        .padding(.trailing)
                    }
                }

                if restaurants.isEmpty {
                    if isSearchMode {
                        if hasLoadedRestaurants {
                            VStack(spacing: 16) {
                                Text("No restaurants found for \"\(searchText)\"")
                                    .foregroundColor(.gray)
                                
                                Button(action: {
                                    searchText = ""
                                    restaurants = []
                                    if let coordinate = locationManager.location {
                                        let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
                                        loadNearbyRestaurants(location: latLng)
                                    }
                                }) {
                                    Text("Back to main")
                                        .foregroundColor(.blue)
                                }
                            }
                            .padding(.top, 50)
                        } else {
                            ProgressView("Searching restaurants by name...")
                                .padding(.top, 50)
                        }
                    } else {
                        ProgressView("Looking for restaurants near you...")
                            .padding(.top, 50)
                    }
                } else {
                    RestaurantListView(
                        restaurants: restaurants,
                        favorites: $favorites,
                        searchText: $searchText,
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
            if !isSearchMode {
                let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
                loadNearbyRestaurants(location: latLng)
            }
        }
        .onAppear {
            if locationManager.location == nil {
                locationManager.refreshLocation()
            } else if !isSearchMode, let coordinate = locationManager.location {
                let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
                loadNearbyRestaurants(location: latLng)
            }
        }
        .onChange(of: searchText) { newValue in
            let trimmed = newValue.trimmingCharacters(in: .whitespaces)
            let api = RestaurantApiService()

            if trimmed.isEmpty {
                if let coordinate = locationManager.location {
                    let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
                    loadNearbyRestaurants(location: latLng)
                }
            } else {
                hasLoadedRestaurants = false
                api.getRestaurantsByName(name: trimmed) { results in
                    print("🔎 Found \(results.count) results for '\(trimmed)'")
                    self.restaurants = results
                    self.hasLoadedRestaurants = true
                }
            }
        }
    }

    private func loadNearbyRestaurants(location: String) {
        print("🍽 Loading restaurants for location: \(location)")
        let api = RestaurantApi()
        hasLoadedRestaurants = false
        api.getRestaurants(location: location) { results in
            print("✅ Received \(results.count) restaurants")
            self.restaurants = results
            self.hasLoadedRestaurants = true
        }
    }
}
