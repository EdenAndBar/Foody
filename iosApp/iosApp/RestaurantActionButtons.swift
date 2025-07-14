import SwiftUI
import Shared

struct RestaurantActionButtons: View {
    let restaurant: Restaurant
    @Binding var favorites: [Restaurant]
    @Binding var isAddingReview: Bool
    let session: UserSession
    let favoritesManager: FirebaseFavoritesManager
    let googleMapsURL: String
    let websiteURL: String

    var isFavorite: Bool {
        favorites.contains { $0.placeId == restaurant.placeId }
    }

    private func toggleFavorite() {
        if isFavorite {
            favorites.removeAll { $0.placeId == restaurant.placeId }
            favoritesManager.removeFavorite(for: session.uid, restaurant: restaurant)
        } else {
            favorites.append(restaurant)
            favoritesManager.addFavorite(for: session.uid, restaurant: restaurant)
        }
    }

    var body: some View {
        HStack(spacing: 40) {
            VStack {
                Button(action: toggleFavorite) {
                    Image(systemName: isFavorite ? "heart.fill" : "heart")
                        .font(.system(size: 24))
                        .foregroundColor(.pink)
                }
                Text("Favorite").font(.footnote).foregroundColor(.pink)
            }

            VStack {
                Button(action: { isAddingReview = true }) {
                    Image(systemName: "pencil")
                        .font(.system(size: 24))
                        .foregroundColor(.green)
                }
                Text("Add review").font(.footnote).foregroundColor(.green)
            }

            VStack {
                Button(action: {
                    if let url = URL(string: googleMapsURL) {
                        UIApplication.shared.open(url)
                    }
                }) {
                    Image(systemName: "map")
                        .font(.system(size: 24))
                        .foregroundColor(.blue)
                }
                Text("Maps").font(.footnote).foregroundColor(.blue)
            }

            VStack {
                Button(action: {
                    if let url = URL(string: websiteURL) {
                        UIApplication.shared.open(url)
                    }
                }) {
                    Image(systemName: "globe")
                        .font(.system(size: 24))
                        .foregroundColor(.purple)
                }
                Text("Website").font(.footnote).foregroundColor(.purple)
            }

            if let phone = restaurant.phoneNumber, !phone.isEmpty {
                VStack {
                    Button(action: {
                        if let url = URL(string: "tel://\(phone.filter { $0.isNumber })") {
                            UIApplication.shared.open(url)
                        }
                    }) {
                        Image(systemName: "phone")
                            .font(.system(size: 24))
                            .foregroundColor(.teal)
                    }
                    Text("Call").font(.footnote).foregroundColor(.teal)
                }
            }
        }
        .padding(.top, 10)
    }
}
