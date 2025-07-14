import SwiftUI
import Shared

struct FavoritesView: View {
    @Binding var favorites: [Restaurant]
        @ObservedObject var filter: RestaurantFilter
        @State private var selectedRestaurantId: String? = nil
        @State private var path = NavigationPath()
        @State private var searchText = ""
        @State private var showFilterSheet = false
        @EnvironmentObject var session: UserSession

    var body: some View {
        NavigationStack(path: $path) {
            RestaurantListView(
                title: "Favorites",
                restaurants: filter.apply(to: favorites),
                favorites: $favorites,
                searchText: $searchText,
                showSheetOnTap: true,
                onTap: { restaurant in
                    path.append(restaurant.placeId)
                },
                filter: filter,
                showFilterSheet: $showFilterSheet
            )
            .navigationDestination(for: String.self) { placeId in
                if let restaurant = favorites.first(where: { $0.placeId == placeId }) {
                    BottomSheetView(
                        restaurant: restaurant,
                        favorites: $favorites
                    )
                }
            }
            .sheet(isPresented: $showFilterSheet) {
                FilterSheetView(filter: filter)
                    .presentationDetents([.fraction(0.4)])
            }
        }
    }
}
