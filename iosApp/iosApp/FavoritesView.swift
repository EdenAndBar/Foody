//import SwiftUI
//import Shared
//
//struct FavoritesView: View {
//    @Binding var favorites: [Restaurant]
//    @State private var selectedRestaurantId: String? = nil
//    @State private var path = NavigationPath()
//    @State private var searchText = ""
//
//
//    var body: some View {
//        NavigationStack(path: $path) {
//            RestaurantListView(
//                restaurants: favorites,
//                favorites: $favorites,
//                searchText: $searchText,
//                onTap: { restaurant in
//                    path.append(restaurant.placeId)
//                }
//            )
//            .navigationDestination(for: String.self) { placeId in
//                if let restaurant = favorites.first(where: { $0.placeId == placeId }) {
//                    BottomSheetView(
//                        restaurant: restaurant,
//                        favorites: $favorites
//                    )
//                }
//            }
//        }
//    }
//}

import SwiftUI
import Shared

struct FavoritesView: View {
    @StateObject var viewModel = FavoritesViewModel()
    @State private var selectedRestaurantId: String? = nil
    @State private var path = NavigationPath()
    @State private var searchText = ""

    var body: some View {
        NavigationStack(path: $path) {
            RestaurantListView(
                restaurants: viewModel.favorites,
                favorites: Binding(get: {
                    viewModel.favorites
                }, set: { newFavorites in
                }),
                searchText: $searchText,
                onTap: { restaurant in
                    path.append(restaurant.placeId)
                }
            )
            .navigationDestination(for: String.self) { placeId in
                if let restaurant = viewModel.favorites.first(where: { $0.placeId == placeId }) {
                    BottomSheetView(
                        restaurant: restaurant,
                        favoritesViewModel: viewModel
                    )
                }
            }
        }
    }
}

