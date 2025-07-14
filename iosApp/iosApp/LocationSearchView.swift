import SwiftUI
import Shared

struct LocationSearchView: View {
    @State private var searchText = ""
    @State private var allRestaurants: [Restaurant] = []
    @State private var filteredRestaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var path: [Restaurant] = []
    @State private var citySuggestions: [String] = []
    @EnvironmentObject var session: UserSession

    @StateObject private var filter = RestaurantFilter()
    @State private var showFilterSheet = false

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 12) {
                SearchAndFilterBar(
                    searchText: $searchText,
                    filter: filter,
                    showFilterSheet: $showFilterSheet,
                    isLocationMode: true
                )
                .padding(.horizontal)
                
                if !citySuggestions.isEmpty {
                    VStack(alignment: .leading, spacing: 0) {
                        ForEach(citySuggestions, id: \.self) { suggestion in
                            Button(action: {
                                searchText = suggestion
                                citySuggestions = []
                                searchByCity()
                            }) {
                                Text(suggestion)
                                    .padding(.vertical, 8)
                                    .padding(.horizontal)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                            }
                            Divider()
                        }
                    }
                    .background(Color.white)
                    .cornerRadius(8)
                    .shadow(radius: 2)
                    .padding(.horizontal)
                    .transition(.opacity)
                    .animation(.easeInOut(duration: 0.2), value: citySuggestions)
                }

                if filteredRestaurants.isEmpty {
                    Spacer()
                    Text("No restaurants found")
                        .foregroundColor(.gray)
                    Spacer()
                } else {
                    RestaurantListView(
                        title: "Results",
                        restaurants: filter.apply(to: filteredRestaurants),
                        favorites: $favorites,
                        searchText: $searchText,
                        showSheetOnTap: false,
                        onTap: { selected in
                            path.append(selected)
                        },
                        showSearchBar: false,
                        filter: filter,
                        showFilterSheet: $showFilterSheet
                    )
                }
            }
//            .onChange(of: searchText) { _ in searchByCity() }
            .onChange(of: searchText) { newValue in
                let trimmed = newValue.trimmingCharacters(in: .whitespacesAndNewlines)
                if trimmed.isEmpty {
                    citySuggestions = []
                    filteredRestaurants = []
                    return
                }

                RestaurantApiService().getCitySuggestions(query: trimmed) { suggestions in
                    DispatchQueue.main.async {
                        citySuggestions = suggestions
                    }
                }
            }
            .sheet(isPresented: $showFilterSheet) {
                FilterSheetView(filter: filter)
                    .presentationDetents([.fraction(0.4)])
            }
            .navigationDestination(for: Restaurant.self) { restaurant in
                BottomSheetView(restaurant: restaurant, favorites: $favorites)
            }
        }
    }
    
    private func searchByCity() {
        let trimmedCity = searchText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmedCity.isEmpty else {
            self.allRestaurants = []
            self.filteredRestaurants = []
            return
        }

        let api = RestaurantApi()
        api.getRestaurants(city: trimmedCity) { results in
            DispatchQueue.main.async {
                let cityLower = trimmedCity.lowercased()
                self.allRestaurants = results.filter {
                    $0.address.lowercased().contains(cityLower)
                }
                self.filteredRestaurants = self.allRestaurants
            }
        }
    }
}
