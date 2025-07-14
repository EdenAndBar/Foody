import SwiftUI
import Shared

struct RestaurantListView: View {
    var title: String = "Restaurants"
    let restaurants: [Restaurant]
    @Binding var favorites: [Restaurant]
    @Binding var searchText: String
    var showSheetOnTap: Bool = true
    let onTap: (Restaurant) -> Void
    var showSearchBar: Bool = true
    @ObservedObject var filter: RestaurantFilter
    @Binding var showFilterSheet: Bool
    @EnvironmentObject var session: UserSession

    var body: some View {
        VStack {
            if showSearchBar {
                SearchAndFilterBar(
                    searchText: $searchText,
                    filter: filter,
                    showFilterSheet: $showFilterSheet
                )
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
                Spacer()
            } else {
                restaurantCardsList
            }
        }
    }
    
    private var restaurantCardsList: some View {
        ScrollView {
            LazyVStack(spacing: 16) {
                ForEach(restaurants, id: \.placeId) { restaurant in
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
                        contextMenuButtons(for: restaurant)
                    }
                }
            }
            .padding()
        }
    }
    
    @ViewBuilder
    private func contextMenuButtons(for restaurant: Restaurant) -> some View {
        if favorites.contains(where: { $0.placeId == restaurant.placeId }) {
            Button {
                favorites.removeAll { $0.placeId == restaurant.placeId }
                FirebaseFavoritesManager().removeFavorite(for: session.uid, restaurant: restaurant)
            } label: {
                Label("Remove from Favorites", systemImage: "heart.slash")
            }
        } else {
            Button {
                favorites.append(restaurant)
                FirebaseFavoritesManager().addFavorite(for: session.uid, restaurant: restaurant)
            } label: {
                Label("Add to Favorites", systemImage: "heart.fill")
            }
        }
    }
}


