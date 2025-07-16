import SwiftUI
import Shared

struct TopTenView: View {
    @State private var restaurants: [Restaurant] = []
    @Binding var favorites: [Restaurant]
    @ObservedObject var filter: RestaurantFilter
    @State private var searchText = ""
    @State private var showFilterSheet = false
    @State private var path = NavigationPath()
    @Binding var isLoggedIn: Bool
    @State private var showSidebar = false
    @State private var hasLoadedTopRestaurants = false

    var isSearchMode: Bool {
        !searchText.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        SidebarContainerView(path: $path, isLoggedIn: $isLoggedIn, showSidebar: $showSidebar) {
            NavigationStack(path: $path) {
                ZStack(alignment: .leading) {
                    if restaurants.isEmpty {
                        VStack {
                            ProgressView("Loading top restaurants...")
                                .padding(.top, 50)
                        }
                    } else {
                        ScrollView {
                            VStack {
                                RestaurantListView(
                                    title: "Top 10",
                                    restaurants: restaurants,
                                    //restaurants: filter.apply(to: restaurants),
                                    favorites: $favorites,
                                    searchText: $searchText,
                                    showSheetOnTap: true,
                                    onTap: { restaurant in
                                        path.append(restaurant)
                                    },
                                    filter: filter,
                                    showFilterSheet: $showFilterSheet
                                )
                            }
                        }
                        .refreshable {
                            hasLoadedTopRestaurants = false
                            fetchTopRestaurants()
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
                .sheet(isPresented: $showFilterSheet) {
                    FilterSheetView(filter: filter)
                        .presentationDetents([.fraction(0.4)])
                }
                .onAppear {
                    guard !hasLoadedTopRestaurants else { return }
                    print("📍 onAppear - TopTenView")
                    fetchTopRestaurants()
                    hasLoadedTopRestaurants = true
                }
            }
        }
    }

    private func fetchTopRestaurants() {
        RestaurantApiService().getRestaurantsByName(name: "") { all in
            let highRated = all.filter { $0.rating >= 4.5 }
            let remaining = all.filter { $0.rating < 4.5 }

            var selected = Array(highRated.shuffled().prefix(10))

            if selected.count < 10 {
                let needed = 10 - selected.count
                let filler = Array(remaining.shuffled().prefix(needed))
                selected += filler
            }

            // 🖨️ הוסיפי את ההדפסות כאן:
            print("🍽️ Total fetched: \(all.count)")
            print("🌟 High rated: \(highRated.count)")
            print("📊 Final selected: \(selected.count)")

            self.restaurants = selected
        }
    }
}
