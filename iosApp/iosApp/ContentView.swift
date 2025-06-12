import SwiftUI
import Shared

struct Restaurant: Identifiable, Equatable {
    let id = UUID()
    let name: String
    let url: String
}

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
                    .onAppear {
                        loadRestaurants()
                    }
            } else {
                VStack {
                    HStack {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.gray)
                        
                        TextField("Search restaurants...", text: $searchText)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                    }
                    .padding(10)
                    .background(Color(.systemGray5))
                    .cornerRadius(10)
                    .padding(.horizontal)
                    
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(filteredRestaurants, id: \.id) { restaurant in
                                RestaurantCard(name: restaurant.name, url: restaurant.url)
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
                        }
                        .padding()
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
        }
    }
    
    var filteredRestaurants: [Restaurant] {
        if searchText.isEmpty {
            return restaurants
        } else {
            return restaurants.filter { $0.name.localizedCaseInsensitiveContains(searchText) }
        }
    }



    private func loadRestaurants() {
        let api = RestaurantApi()
        api.getRestaurants { results in
            let mapped = results.compactMap { pair -> (name: String, url: String)? in
                guard let name = pair.first as? String,
                      let url = pair.second as? String else {
                    return nil
                }
                return (name: name, url: url)
            }
            self.restaurants = mapped.map { Restaurant(name: $0.name, url: $0.url) }
        }
    }

}

struct RestaurantCard: View {
    let name: String
    let url: String

    var body: some View {
        ZStack(alignment: .center) {
            AsyncImage(url: URL(string: url)) { image in
                image
                    .resizable()
                    .scaledToFill()
                    .frame(height: 200)
                    .clipped()
            } placeholder: {
                ProgressView()
                    .frame(height: 200)
            }

            Rectangle()
                .foregroundColor(.black)
                .opacity(0.3)
                .frame(height: 200)

            Text(name)
                .font(.title3)
                .foregroundColor(.white)
                .bold()
                .shadow(radius: 3)
        }
        .cornerRadius(12)
        .shadow(radius: 4)
    }
    
}

