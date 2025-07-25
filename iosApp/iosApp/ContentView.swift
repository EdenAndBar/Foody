import SwiftUI
import Shared
import CoreLocation
import Foundation
import Sliders

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @Binding var isLoggedIn: Bool
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()
    @StateObject private var locationManager = LocationManager()
    @State private var hasLoadedRestaurants = false
    @State private var searchText = ""
    @ObservedObject var filter: RestaurantFilter
    @State private var showFilterSheet = false
    @EnvironmentObject var session: UserSession
    @State private var showSidebar = false

    var isSearchMode: Bool {
        !searchText.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        SidebarContainerView(path: $path, isLoggedIn: $isLoggedIn, showSidebar: $showSidebar) {
            NavigationStack(path: $path) {
                ZStack(alignment: .leading) {
                    if restaurants.isEmpty {
                        VStack {
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
                        }
                    } else {
                            VStack {
                                RestaurantListView(
                                    title: "Home",
                                    restaurants: filter.apply(to: restaurants),
                                    favorites: $favorites,
                                    searchText: $searchText,
                                    showSheetOnTap: true,
                                    onTap: { restaurant in
                                        path.append(restaurant)
                                    },
                                    filter: filter,
                                    showFilterSheet: $showFilterSheet
                                )
                            }
                        .refreshable {
                            refreshRestaurants()
                        }
                    }
                }
                .navigationDestination(for: Restaurant.self) { restaurant in
                    BottomSheetView(restaurant: restaurant, favorites: $favorites)
                }
                .navigationDestination(for: String.self) { value in
                    if value == "profile" {
                        ProfileView(showSidebar: $showSidebar)
                    } else if value == "about" {
                        AboutUsView(showSidebar: $showSidebar)
                    }
                }
                .sheet(isPresented: $showFilterSheet) {
                    FilterSheetView(filter: filter)
                        .presentationDetents([.fraction(0.4)])
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
                FirebaseFavoritesManager().fetchFavorites(for: session.uid) { favorites in
                    DispatchQueue.main.async {
                        self.favorites = favorites
                        self.session.favorites = favorites
                    }
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
    }

    private func loadNearbyRestaurants(location: String) {
        let api = RestaurantApi()
        hasLoadedRestaurants = false
        api.getRestaurants(location: location) { results in
            self.restaurants = results
            self.hasLoadedRestaurants = true
        }
    }

    private func refreshRestaurants() {
        if isSearchMode {
            let trimmed = searchText.trimmingCharacters(in: .whitespaces)
            RestaurantApiService().getRestaurantsByName(name: trimmed) { results in
                self.restaurants = results
                self.hasLoadedRestaurants = true
            }
        } else if let coordinate = locationManager.location {
            let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
            loadNearbyRestaurants(location: latLng)
        }
    }
}
