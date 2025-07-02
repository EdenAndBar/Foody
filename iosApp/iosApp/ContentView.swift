import SwiftUI
import Shared
import CoreLocation
import Foundation

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @Binding var isLoggedIn: Bool
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()
    @StateObject private var locationManager = LocationManager()
    @State private var hasLoadedRestaurants = false
    @State private var searchText = ""
    @State private var showSidebar = false

    var isSearchMode: Bool {
        !searchText.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        NavigationStack(path: $path) {
            ZStack(alignment: .leading) {
                VStack {
                    HStack {
                        if hasLoadedRestaurants && restaurants.count > 0 && locationManager.location != nil && !isSearchMode {
                            HStack {
                                // כפתור תפריט (שלושה פסים)
                                Button(action: {
                                    showSidebar.toggle()
                                }) {
                                    Image(systemName: "line.3.horizontal")
                                        .font(.title2)
                                }
                                .padding(.leading)
                                
                                Spacer()
                                
                                // כפתור רענון מיקום
                                Button(action: {
                                    locationManager.refreshLocation()
                                }) {
                                    Label("Refresh location", systemImage: "location.circle")
                                }
                                .padding(.trailing)
                            }
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
                .navigationDestination(for: String.self) { value in
                    switch value {
                    case "profile":
                        ProfileView(showSidebar: $showSidebar, isLoggedIn: $isLoggedIn)
                    case "about":
                        AboutUsView(showSidebar: $showSidebar)
                    default:
                        EmptyView()
                    }
                }
                
                if hasLoadedRestaurants && !restaurants.isEmpty {
                    if showSidebar {
                        Color.black.opacity(0.3)
                            .edgesIgnoringSafeArea(.all)
                            .onTapGesture {
                                withAnimation {
                                    showSidebar = false
                                }
                            }
                    }
                    
                    GeometryReader { geometry in
                        VStack(alignment: .leading, spacing: 16) {
                            Spacer().frame(height: 80) // ריווח עליון
                            
                            // כפתור פרופיל
                            Button(action: {
                                path.append("profile")
                            }) {
                                Label("Profile", systemImage: "person")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                            }
                            
                            // כפתור About Us
                            Button(action: {
                                path.append("about")
                            }) {
                                Label("About Us", systemImage: "info.circle")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                            }
                            
                            // כפתור Logout
                            Button(action: {
                                withAnimation { showSidebar = false }
                                isLoggedIn = false
                            }) {
                                Label("Logout", systemImage: "arrow.backward.circle")
                                    .font(.headline)
                                    .foregroundColor(.red)
                            }
                            .padding(.bottom, 30)
                            Spacer()
                        }
                        .frame(width: 200, height: .infinity)
                        //.background(Color(UIColor.systemGray6))
                        .offset(x: showSidebar ? 0 : -300)
                        .animation(.easeInOut(duration: 0.3), value: showSidebar)
                        .edgesIgnoringSafeArea(.all)
                        .zIndex(1)
                    }
                    
                }
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
