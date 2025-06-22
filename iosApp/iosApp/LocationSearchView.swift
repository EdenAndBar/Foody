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

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 12) {
                // שדות חיפוש מעוצבים עם אייקונים
                HStack(spacing: 12) {
                    searchField(
                        systemImage: "building.2.crop.circle",
                        placeholder: "Enter city...",
                        text: $cityText
                    )
                    searchField(
                        systemImage: "fork.knife.circle",
                        placeholder: "Restaurant name",
                        text: $nameText
                    )
                }
                .padding(.horizontal)

                if filteredRestaurants.isEmpty {
                    Spacer()
                    Text("No restaurants found")
                        .foregroundColor(.gray)
                    Spacer()
                } else {
                    RestaurantListView(
                        restaurants: filteredRestaurants,
                        favorites: $favorites,
                        searchText: $searchText,
                        onTap: { selected in
                            path.append(selected)
                        },
                        showSearchBar: false
                    )
                }
            }
            .onChange(of: cityText) { _ in
                searchByCity()
            }
            .onChange(of: nameText) { _ in
                filterByName()
            }
            .navigationDestination(for: Restaurant.self) { restaurant in
                BottomSheetView(restaurant: restaurant, favorites: $favorites)
            }
        }
    }

    private func searchField(systemImage: String, placeholder: String, text: Binding<String>) -> some View {
        HStack {
            Image(systemName: systemImage)
                .foregroundColor(.gray)
            TextField(placeholder, text: text)
                .autocapitalization(.none)
                .disableAutocorrection(true)
        }
        .padding(12)
        .background(Color(.systemGray6))
        .cornerRadius(20)
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .stroke(Color(.systemGray4), lineWidth: 0.8)
        )
    }

    private func searchByCity() {
        guard !cityText.trimmingCharacters(in: .whitespaces).isEmpty else {
            self.allRestaurants = []
            self.filteredRestaurants = []
            return
        }

        let api = RestaurantApi()
        api.getRestaurants(city: cityText) { results in
            DispatchQueue.main.async {
                self.allRestaurants = results
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

