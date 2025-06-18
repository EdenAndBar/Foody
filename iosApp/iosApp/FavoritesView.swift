import SwiftUI
import Shared

struct FavoritesView: View {
    @Binding var favorites: [Restaurant]
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()

    var body: some View {
        NavigationStack(path: $path) {
            RestaurantListView(
                restaurants: favorites,
                favorites: $favorites,
                onTap: { restaurant in
                    path.append(restaurant.placeId)
                }
            )
            .navigationDestination(for: String.self) { placeId in
                if let restaurant = favorites.first(where: { $0.placeId == placeId }) {
                    BottomSheetView(
                        restaurant: restaurant,
                        favorites: $favorites
                    )
                }
            }
        }
    }
}
