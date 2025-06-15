//import SwiftUI
//import Shared
//
//struct ContentView: View {
//    @State private var restaurants: [Restaurant] = []
//    @Binding var favorites: [Restaurant]
//    @State private var searchText = ""
//    @State private var selectedRestaurant: Restaurant? = nil
//    @State private var showingSheet = false
//
//    var body: some View {
//        NavigationView {
//            if restaurants.isEmpty {
//                ProgressView("Loading restaurants...")
//                    .onAppear {
//                        loadRestaurants()
//                    }
//            } else {
//                VStack {
//                    HStack {
//                        Image(systemName: "magnifyingglass")
//                            .foregroundColor(.gray)
//                        
//                        TextField("Search restaurants...", text: $searchText)
//                            .autocapitalization(.none)
//                            .disableAutocorrection(true)
//                    }
//                    .padding(10)
//                    .background(Color(.systemGray5))
//                    .cornerRadius(10)
//                    .padding(.horizontal)
//                    
//                    ScrollView {
//                        LazyVStack(spacing: 16) {
//                            ForEach(filteredRestaurants, id: \.id) { restaurant in
//                                RestaurantCard(
//                                    name: restaurant.name,
//                                    photoUrl: restaurant.photoUrl,
//                                    description: restaurant.description,
//                                    rating: restaurant.rating
//                                )
//                                    .contextMenu {
//                                        if favorites.contains(restaurant) {
//                                            Button {
//                                                favorites.removeAll { $0 == restaurant }
//                                            } label: {
//                                                Label("Remove from Favorites", systemImage: "heart.slash")
//                                            }
//                                        } else {
//                                            Button {
//                                                favorites.append(restaurant)
//                                            } label: {
//                                                Label("Add to Favorites", systemImage: "heart.fill")
//                                            }
//                                        }
//                                    }
//
//                            }
//                        }
//                        .padding()
//                    }
//                }
//                .sheet(isPresented: $showingSheet) {
//                    if let restaurant = selectedRestaurant {
//                        BottomSheetView(
//                            restaurant: restaurant,
//                            favorites: $favorites
//                        )
//                    }
//                }
//                
//            }
//        }
//    }
//    
//    var filteredRestaurants: [Restaurant] {
//        if searchText.isEmpty {
//            return restaurants
//        } else {
//            return restaurants.filter { $0.name.localizedCaseInsensitiveContains(searchText) }
//        }
//    }
//
//
//
//    private func loadRestaurants() {
//        let api = RestaurantApi()
//        api.getRestaurants { results in
//            self.restaurants = results
//        }
//    }
//
//}
//
//struct RestaurantCard: View {
//    let name: String
//    let photoUrl: String
//    let description: String
//    let rating: Float
//
//    var body: some View {
//        VStack(alignment: .leading, spacing: 0) {
//            AsyncImage(url: URL(string: photoUrl)) { image in
//                image
//                    .resizable()
//                    .scaledToFill()
//                    .frame(height: 200)
//                    .clipped()
//            } placeholder: {
//                ProgressView()
//                    .frame(height: 200)
//            }
//
//            VStack(alignment: .leading, spacing: 6) {
//                Text(name)
//                    .font(.headline)
//
//                Text(description)
//                    .font(.subheadline)
//                    .foregroundColor(.gray)
//                    .lineLimit(2)
//
//                HStack {
//                    Image(systemName: "star.fill")
//                        .foregroundColor(.yellow)
//                    Text(String(format: "%.1f", rating))
//                        .font(.subheadline)
//                }
//            }
//            .padding()
//        }
//        .background(Color.white)
//        .cornerRadius(12)
//        .shadow(radius: 4)
//    }
//}
//
//
import SwiftUI
import Shared

struct ContentView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @State private var searchText = ""
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var showingSheet = false

    var body: some View {
        NavigationView {
            if restaurants.isEmpty {
                ProgressView("Loading restaurants...")
                    .onAppear { loadRestaurants() }
            } else {
                RestaurantListView(
                    restaurants: restaurants,
                    favorites: $favorites,
                    onTap: { restaurant in
                        selectedRestaurant = restaurant
                        showingSheet = true
                    }
                )
            }
        }
        .sheet(isPresented: $showingSheet) {
            if let restaurant = selectedRestaurant {
                BottomSheetView(
                    restaurant: restaurant,
                    favorites: $favorites
                )
            }
        }
    }

    private func loadRestaurants() {
        let api = RestaurantApi()
        api.getRestaurants { results in
            self.restaurants = results
        }
    }
}
