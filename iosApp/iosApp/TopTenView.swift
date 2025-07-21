import SwiftUI
import Shared

struct TopTenView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @ObservedObject var filter: RestaurantFilter
    @Binding var isLoggedIn: Bool
    @State private var showSidebar = false
    @State private var hasLoadedTopRestaurants = false
    @State private var selectedRestaurant: Restaurant? = nil
    @State private var showBottomSheet = false
    @State private var path = NavigationPath()

    var body: some View {
        SidebarContainerView(path: $path, isLoggedIn: $isLoggedIn, showSidebar: $showSidebar) {
            NavigationStack(path: $path) {
                ZStack {
                    if restaurants.isEmpty {
                        ProgressView("Loading top restaurants...")
                            .padding(.top, 50)
                    } else {
                        VStack(spacing: 16) {
                            Text("🏆 Top 10")
                                .font(.largeTitle)
                                .bold()
                                .multilineTextAlignment(.center)
                                .padding(.top, 24)
                            ScrollView {
                                LazyVStack(spacing: 16) {
                                    ForEach(Array(restaurants.enumerated()), id: \.element.placeId) { index, restaurant in
                                        TopTenRestaurantCard(index: index, restaurant: restaurant, onTap: {
                                            path.append(restaurant)
                                        })
                                    }
                                }
                                .padding(.top)
                            }
                            .refreshable {
                                hasLoadedTopRestaurants = false
                                fetchTopRestaurants()
                            }
                        }
                    }
                }
                .navigationDestination(for: Restaurant.self) { restaurant in
                    BottomSheetView(restaurant: restaurant, favorites: $favorites)
                }
                .navigationDestination(for: String.self) { value in
                    if value == "profile" {
                        ProfileView(showSidebar: $showSidebar)
                    } else if value == "about" {
                        AboutUsView(showSidebar: $showSidebar)
                    }
                }
                .onAppear {
                    guard !hasLoadedTopRestaurants else { return }
                    print("\u{1F4CD} onAppear - TopTenView")
                    fetchTopRestaurants()
                    hasLoadedTopRestaurants = true
                }
            }
        }
    }

    private func fetchTopRestaurants() {
        RestaurantApiService().getRestaurantsByName(name: "") { all in
            let highRated = all.filter { $0.rating >= 4.0 }
            let remaining = all.filter { $0.rating < 4.0 }

            var selected = Array(highRated.shuffled().prefix(10))

            if selected.count < 10 {
                let needed = 10 - selected.count
                let filler = Array(remaining.shuffled().prefix(needed))
                selected += filler
            }

            print("\u{1F37D}\u{FE0F} Total fetched: \(all.count)")
            print("\u{1F31F} High rated: \(highRated.count)")
            print("\u{1F4CA} Final selected: \(selected.count)")

            self.restaurants = selected.sorted { $0.rating > $1.rating }
        }
    }
}

struct TopTenRestaurantCard: View {
    let index: Int
    let restaurant: Restaurant
    let onTap: () -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            AsyncImage(url: URL(string: restaurant.photoUrl ?? "")) { image in
                image
                    .resizable()
                    .scaledToFill()
                    .frame(width: UIScreen.main.bounds.width - 32, height: 200)
                    .frame(height: 200)
                    .clipped()
            } placeholder: {
                Color.gray.opacity(0.3)
                    .frame(width: UIScreen.main.bounds.width - 32, height: 200)
                    .cornerRadius(12)
            }

            Rectangle()
                .foregroundColor(.black)
                .opacity(0.3)
                .frame(height: 200)

            Text("\(index + 1)")
                .font(.headline)
                .bold()
                .foregroundColor(.white)
                .padding(8)
                .padding([.top, .leading], 10)

            VStack {
                Text(restaurant.name)
                    .font(.title2)
                    .bold()
                    .foregroundColor(.white)
                    .multilineTextAlignment(.center)
                    .padding(.bottom, 4)

                StarRatingView(rating: Double(restaurant.rating))
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        }
        .frame(height: 200)
        .cornerRadius(12)
        .padding(.horizontal)
        .onTapGesture {
            onTap()
        }
    }
}

struct StarRatingView: View {
    let rating: Double

    var body: some View {
        let fullStars = Int(rating)
        let hasHalfStar = rating - Double(fullStars) >= 0.25 && rating - Double(fullStars) < 0.75
        let emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0)

        HStack(spacing: 4) {
            ForEach(0..<fullStars, id: \.self) { _ in
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
            }
            if hasHalfStar {
                Image(systemName: "star.leadinghalf.filled")
                    .foregroundColor(.yellow)
            }
            ForEach(0..<emptyStars, id: \.self) { _ in
                Image(systemName: "star")
                    .foregroundColor(.yellow)
            }
        }
    }
}
