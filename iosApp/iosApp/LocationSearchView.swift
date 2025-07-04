import SwiftUI
import Shared

struct LocationSearchView: View {
    @State private var cityText = ""
    @State private var nameText = ""
    @State private var searchText = ""
    @State private var allRestaurants: [Restaurant] = []
    @State private var filteredRestaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var path: [Restaurant] = []

    @StateObject private var filter = RestaurantFilter()
    @State private var showFilterSheet = false

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 12) {
                VStack(spacing: 8) {
                    HStack(spacing: 12) {
                        searchFieldStyled(systemImage: "building.2.crop.circle", placeholder: "Enter city...", text: $cityText)
                        searchFieldStyled(systemImage: "fork.knife.circle", placeholder: "Restaurant name", text: $nameText)
                    }
                    SortAndFilterBar(filter: filter, showFilterSheet: $showFilterSheet)
                }
                .padding(.horizontal)

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
            .onChange(of: cityText) { _ in searchByCity() }
            .onChange(of: nameText) { _ in filterByName() }
            .sheet(isPresented: $showFilterSheet) {
                FilterSheetView(filter: filter)
                    .presentationDetents([.fraction(0.4)])
            }
            .navigationDestination(for: Restaurant.self) { restaurant in
                BottomSheetView(restaurant: restaurant, favorites: $favorites)
            }
        }
    }

    private func searchFieldStyled(systemImage: String, placeholder: String, text: Binding<String>) -> some View {
        HStack {
            Image(systemName: systemImage)
                .foregroundColor(Color(.systemGray))
            TextField(placeholder, text: text)
                .foregroundColor(.primary)
                .font(.subheadline)
                .autocapitalization(.none)
                .disableAutocorrection(true)
        }
        .padding(12)
        .background(Color(.systemGray6))
        .cornerRadius(20)
        .shadow(color: Color(.black).opacity(0.05), radius: 3, x: 0, y: 2)
    }

    private func searchByCity() {
        let trimmedCity = cityText.trimmingCharacters(in: .whitespacesAndNewlines)
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
                self.filterByName()
            }
        }
    }

    private func filterByName() {
        if nameText.isEmpty {
            self.filteredRestaurants = allRestaurants
        } else {
            self.filteredRestaurants = allRestaurants.filter {
                $0.name.localizedCaseInsensitiveContains(nameText)
            }
        }
    }
}
