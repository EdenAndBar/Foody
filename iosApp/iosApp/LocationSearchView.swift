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
                    searchFieldStyled(systemImage: "building.2.crop.circle", placeholder: "Enter city...", text: $cityText)
                    searchFieldStyled(systemImage: "fork.knife.circle", placeholder: "Restaurant name", text: $nameText)
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
                // ✅ סינון לפי כתובת המסעדה (ולא לפי השם)
                let cityLower = trimmedCity.lowercased()
                self.allRestaurants = results.filter {
                    $0.address.lowercased().contains(cityLower)
                }
                self.filterByName()
            }
        }
    }


//    private func searchByCity() {
//        guard !cityText.trimmingCharacters(in: .whitespaces).isEmpty else {
//            self.allRestaurants = []
//            self.filteredRestaurants = []
//            return
//        }
//
//        let api = RestaurantApi()
//        api.getRestaurants(city: cityText) { results in
//            DispatchQueue.main.async {
//                self.allRestaurants = results
//                self.filterByName()
//            }
//        }
//
//    }


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

