import SwiftUI
import Shared

struct RestaurantListView: View {
    let restaurants: [Restaurant]
    @Binding var favorites: [Restaurant]
    @Binding var searchText: String
    var showSheetOnTap: Bool = true
    let onTap: (Restaurant) -> Void
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var showingSheet = false
    var showSearchBar: Bool = true

    var body: some View {
        VStack {
            searchBar
            restaurantCardsList
        }
    }
    
    private var restaurantCardsList: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                ForEach(restaurants, id: \.id) { restaurant in
                    RestaurantCard(
                        name: restaurant.name,
                        photoUrl: restaurant.photoUrl,
                        address: restaurant.address,
                        rating: restaurant.rating,
                        isOpenNow: restaurant.isOpenNow?.boolValue,
                        category: restaurant.category
                    )
                    .frame(maxWidth: .infinity)
                    .background(Color.white)
                    .cornerRadius(12)
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

                if restaurants.isEmpty {
                    Spacer()
                    if !searchText.trimmingCharacters(in: .whitespaces).isEmpty {
                        Text("No restaurants found for \"\(searchText)\"")
                            .foregroundColor(.gray)
                            .padding(.top, 50)
                    } else {
                        Text("No favorite restaurants yet")
                            .foregroundColor(.gray)
                            .padding(.top, 50)
                    }
                }
            }
            .padding()
        }
    }

    private var searchBar: some View {
        Group {
            if showSearchBar {
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.gray)

                    TextField("Search restaurants...", text: $searchText)
                        .autocapitalization(.none)
                        .disableAutocorrection(true)
                }
                .padding(10)
                .background(Color(UIColor { trait in
                    trait.userInterfaceStyle == .dark ? .darkGray : .systemGray6
                }))
                .cornerRadius(20)
                .shadow(color: .black.opacity(0.1), radius: 5, x: 0, y: 3)
                .padding(.horizontal)
            }
        }
    }
}
