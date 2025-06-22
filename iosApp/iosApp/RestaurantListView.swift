import SwiftUI
import Shared

//struct RestaurantListView: View {
//    let restaurants: [Restaurant]
//    @Binding var favorites: [Restaurant]
//    var showSheetOnTap: Bool = true
//    let onTap: (Restaurant) -> Void
//    @Binding var searchText: String
//    @State private var selectedRestaurant: Restaurant? = nil
//    @State private var showingSheet = false
//    var showSearchBar: Bool = true
//
//    private var filtered: [Restaurant] {
//        if searchText.isEmpty {
//            return restaurants
//        } else {
//            return restaurants.filter {
//                $0.name.localizedCaseInsensitiveContains(searchText)
//            }
//        }
//    }
//
//    var body: some View {
//        VStack {
//            searchBar
//            restaurantCardsList // ⬅️ חילקנו את החלק הכבד החוצה
//        }
//    }
//
//    private var restaurantCardsList: some View {
//        ScrollView {
//            LazyVStack(spacing: 16) {
//                ForEach(filtered, id: \.id) { restaurant in
//                    RestaurantCard(
//                        name: restaurant.name,
//                        photoUrl: restaurant.photoUrl,
//                        address: restaurant.address,
//                        rating: restaurant.rating,
//                        isOpenNow: restaurant.isOpenNow?.boolValue
//                    )
//                    .onTapGesture {
//                        onTap(restaurant)
//                    }
//                    .contextMenu {
//                        if favorites.contains(restaurant) {
//                            Button {
//                                favorites.removeAll { $0 == restaurant }
//                            } label: {
//                                Label("Remove from Favorites", systemImage: "heart.slash")
//                            }
//                        } else {
//                            Button {
//                                favorites.append(restaurant)
//                            } label: {
//                                Label("Add to Favorites", systemImage: "heart.fill")
//                            }
//                        }
//                    }
//                }
//
//                if filtered.isEmpty {
//                    Text("No restaurants found")
//                        .foregroundColor(.gray)
//                        .padding()
//                }
//            }
//            .padding()
//        }
//    }
//
//
//    private var searchBar: some View {
//        Group {
//            if showSearchBar {
//                HStack {
//                    Image(systemName: "magnifyingglass")
//                        .foregroundColor(.gray)
//
//                    TextField("Search restaurants...", text: $searchText)
//                        .autocapitalization(.none)
//                        .disableAutocorrection(true)
//                }
//                .padding(10)
//                .background(Color(UIColor { trait in
//                    trait.userInterfaceStyle == .dark ? .darkGray : .systemGray6
//                }))
//                .cornerRadius(20)
//                .shadow(color: .black.opacity(0.1), radius: 5, x: 0, y: 3)
//                .padding(.horizontal)
//            }
//        }
//    }
//
//}
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
                        isOpenNow: restaurant.isOpenNow?.boolValue
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

                if restaurants.isEmpty {
                    Text("No restaurants found")
                        .foregroundColor(.gray)
                        .padding()
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
