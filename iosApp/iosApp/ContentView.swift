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
    @State private var searchText = ""
    @State private var filteredRestaurants: [Restaurant] = []
    @State private var isSearching = false

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



//
//import SwiftUI
//import Shared
//import CoreLocation
//
//struct ContentView: View {
//    @State private var restaurants: [Restaurant] = []
//    @Binding var favorites: [Restaurant]
//    @State private var selectedRestaurantId: String? = nil
//    @State private var path = NavigationPath()
//    @StateObject private var locationManager = LocationManager()
//    @State private var hasLoadedRestaurants = false
//    @State private var searchText = ""
//    @State private var filteredRestaurants: [Restaurant] = []
//    @State private var isSearching = false
//
//    var body: some View {
//        NavigationStack(path: $path) {
//            VStack {
//                HStack {
//                    Spacer()
//                    if hasLoadedRestaurants && locationManager.location != nil {
//                        Button(action: {
//                            locationManager.refreshLocation()
//                        }) {
//                            Label("Refresh location", systemImage: "location.circle")
//                        }
//                        .padding(.trailing)
//                    }
//                }
//
//                if isSearching {
//                    ProgressView("Searching restaurants...")
//                        .padding()
//                }
//
//                if (filteredRestaurants.isEmpty && !searchText.isEmpty) {
//                    Text("No results found for '\(searchText)'")
//                        .foregroundColor(.gray)
//                        .padding()
//                }
//
//                RestaurantListView(
//                    restaurants: filteredRestaurants.isEmpty && !isSearching && searchText.isEmpty ? restaurants : filteredRestaurants,
//                    favorites: $favorites,
//                    searchText: $searchText,
//                    onTap: { restaurant in
//                        path.append(restaurant)
//                    }
//                )
//            }
//            .navigationDestination(for: Restaurant.self) { restaurant in
//                BottomSheetView(restaurant: restaurant, favorites: $favorites)
//            }
//        }
//        .onReceive(locationManager.$location.compactMap { $0 }) { coordinate in
//            let latLng = "\(coordinate.latitude),\(coordinate.longitude)"
//            loadNearbyRestaurants(location: latLng)
//        }
//        .onAppear {
//            if locationManager.location == nil {
//                locationManager.refreshLocation()
//            }
//        }
//        .onChange(of: searchText) { newValue in
//            if newValue.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines).isEmpty {
//                filteredRestaurants = []
//            } else {
//                isSearching = true
//                let api = RestaurantApi()
//                api.getRestaurantsByCity(city: newValue) { results in
//                    DispatchQueue.main.async {
//                        self.filteredRestaurants = results
//                        self.isSearching = false
//                    }
//                }
//            }
//        }
//    }
//
//    private func loadNearbyRestaurants(location: String) {
//        let api = RestaurantApi()
//        api.getRestaurants(location: location) { results in
//            self.restaurants = results
//            self.hasLoadedRestaurants = true
//        }
//    }
//}
//
