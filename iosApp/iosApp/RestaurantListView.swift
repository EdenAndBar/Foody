import SwiftUI
import Shared

struct RestaurantListView: View {
    let restaurants: [Restaurant]
    @Binding var favorites: [Restaurant]
    var showSheetOnTap: Bool = true
    let onTap: (Restaurant) -> Void
    @State private var searchText = ""
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var showingSheet = false

    private var filtered: [Restaurant] {
        if searchText.isEmpty {
            return restaurants
        } else {
            return restaurants.filter {
                $0.name.localizedCaseInsensitiveContains(searchText)
            }
        }
    }

    var body: some View {
        VStack {
            searchBar
            restaurantCardsList // ⬅️ חילקנו את החלק הכבד החוצה
        }
    }

    private var restaurantCardsList: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                ForEach(filtered, id: \.id) { restaurant in
                    RestaurantCard(
                        name: restaurant.name,
                        photoUrl: restaurant.photoUrl,
                        address: restaurant.address,
                        rating: restaurant.rating
                    )
                    .onTapGesture {
                        onTap(restaurant)
                    }
                    .contextMenu {
                        if favorites.contains(restaurant) {
                            Button {
                                favorites.removeAll { $0 == restaurant }
                            } label: {
                                Label("Remove from Favorites", systemImage: "heart.slash")
                            }
                        } else {
                            Button {
                                favorites.append(restaurant)
                            } label: {
                                Label("Add to Favorites", systemImage: "heart.fill")
                            }
                        }
                    }
                }

                if filtered.isEmpty {
                    Text("No restaurants found")
                        .foregroundColor(.gray)
                        .padding()
                }
            }
            .padding()
        }
    }


    private var searchBar: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)

            TextField("Search restaurants...", text: $searchText)
                .autocapitalization(.none)
                .disableAutocorrection(true)
        }
        .padding(10)
        .background(Color(.systemGray5))
        .cornerRadius(10)
        .padding(.horizontal)
    }
}
