//import SwiftUI
//
//struct FavoritesView: View {
//    @Binding var favorites: [Restaurant]
//    @State private var searchText = ""
//    
//    var filteredFavorites: [Restaurant] {
//        if searchText.isEmpty {
//            return favorites
//        } else {
//            return favorites.filter { $0.name.localizedCaseInsensitiveContains(searchText) }
//        }
//    }
//
//    var body: some View {
//        VStack {
//            HStack {
//                Image(systemName: "magnifyingglass")
//                    .foregroundColor(.gray)
//                
//                TextField("Search restaurants...", text: $searchText)
//                    .autocapitalization(.none)
//                    .disableAutocorrection(true)
//            }
//            .padding(10)
//            .background(Color(.systemGray5))
//            .cornerRadius(10)
//            .padding(.horizontal)
//            ScrollView {
//                LazyVStack(spacing: 16) {
//                    ForEach(filteredFavorites, id: \.id) { restaurant in
//                        RestaurantCard(name: restaurant.name, url: restaurant.url)
//                            .contextMenu {
//                                Button {
//                                    favorites.removeAll { $0 == restaurant }
//                                } label: {
//                                    Label("Remove from Favorites", systemImage: "heart.slash")
//                                }
//                            }
//                    }
//                }
//                .padding()
//            }
//            .navigationTitle("Favorites")
//        }
//    }
//}
import SwiftUI
import Shared

struct FavoritesView: View {
    @Binding var favorites: [Restaurant]
    @State private var selectedRestaurant: Restaurant? = nil

    var body: some View {
        RestaurantListView(
            restaurants: favorites,
            favorites: $favorites,
            onTap: { restaurant in
                selectedRestaurant = restaurant
            }
        )
    }
}
