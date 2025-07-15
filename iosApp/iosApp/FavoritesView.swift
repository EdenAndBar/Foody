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
    @Binding var isLoggedIn: Bool
    @State private var showSidebar = false

    var body: some View {
        SidebarContainerView(path: $path, isLoggedIn: $isLoggedIn, showSidebar: $showSidebar) {
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
                .navigationDestination(for: String.self) { value in
                    if let restaurant = favorites.first(where: { $0.placeId == value }) {
                        BottomSheetView(restaurant: restaurant, favorites: $favorites)
                    } else if value == "profile" {
                        ProfileView(showSidebar: $showSidebar)
                    } else if value == "about" {
                        AboutUsView(showSidebar: $showSidebar)
                    }
                }
                .sheet(isPresented: $showFilterSheet) {
                    FilterSheetView(filter: filter)
                        .presentationDetents([.fraction(0.4)])
                }
            }
        }
    }
}
