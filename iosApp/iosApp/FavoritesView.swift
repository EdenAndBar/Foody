import SwiftUI
import Shared

struct FavoritesView: View {
    @Binding var favorites: [Restaurant]
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var showingSheet = false

    var body: some View {
        RestaurantListView(
            restaurants: favorites,
            favorites: $favorites,
            onTap: { restaurant in
                selectedRestaurant = restaurant
                showingSheet = true
            }
        )
        .sheet(isPresented: $showingSheet) {
            if let restaurant = selectedRestaurant {
                BottomSheetView(
                    restaurant: restaurant,
                    favorites: $favorites
                )
            }
        }
    }
}
