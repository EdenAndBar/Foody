
import SwiftUI
import Shared

extension KotlinBoolean {
    open override var boolValue: Bool {
        return self == KotlinBoolean(bool: true)
    }
}
struct BottomSheetView: View {
    let restaurant: Restaurant
    @Binding var favorites: [Restaurant]
    @EnvironmentObject var session: UserSession
    let favoritesManager = FirebaseFavoritesManager()
    @Environment(\.dismiss) var dismiss
    @StateObject private var viewModel = RestaurantDetailsViewModel()
    @State private var userReviews: [GoogleReviewUI] = []
    @State private var isAddingReview = false
    let reviewManager = FirebaseReviewManager()
    
    private func toggleFavorite() {
        if isFavorite {
            favorites.removeAll { $0.placeId == restaurant.placeId }
            favoritesManager.removeFavorite(for: session.uid, restaurant: restaurant)
        } else {
            favorites.append(restaurant)
            favoritesManager.addFavorite(for: session.uid, restaurant: restaurant)
        }
    }
    
    private var isOpen: Bool {
        restaurant.isOpenNow?.boolValue ?? false
    }
    
    var isFavorite: Bool {
        favorites.contains(where: { $0.placeId == restaurant.placeId })
    }
    
    var allReviews: [GoogleReviewUI] {
        viewModel.googleReviews.map { GoogleReviewUI(from: $0) } + userReviews
    }
    
    var body: some View {
        VStack(spacing: 20) {
            Text(restaurant.name)
                .font(.title2)
                .bold()
            
            Text(isOpen ? "Open Now" : "Closed")
                .padding(6)
                .background(isOpen ? Color.green.opacity(0.2) : Color.red.opacity(0.2))
                .foregroundColor(isOpen ? .green : .red)
                .cornerRadius(8)
                .font(.subheadline)
                .bold()
            
            AsyncImage(url: URL(string: restaurant.photoUrl)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                ProgressView()
            }
            .frame(height: 150)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            
            HStack {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                Text(String(format: "%.1f", restaurant.rating))
                    .font(.subheadline)
            }
            
            if !restaurant.address.isEmpty {
                Text(restaurant.address)
                    .font(.body)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }
            
            RestaurantActionButtons(
                restaurant: restaurant,
                favorites: $favorites,
                isAddingReview: $isAddingReview,
                session: session,
                favoritesManager: favoritesManager,
                googleMapsURL: viewModel.googleMapsURL,
                websiteURL: viewModel.websiteURL
            )
            
            ReviewListView(
                reviews: allReviews,
                sessionUid: session.uid,
                onDelete: { reviewId in
                    deleteReview(reviewId: reviewId)
                }
            )
            Spacer()
        }
        .padding()
        .presentationDetents([.fraction(0.70)])
        .onAppear {
            Task {
                await viewModel.fetchDetails(for: restaurant.placeId)
                reviewManager.fetchReviews(for: restaurant.placeId) { fetched in
                    DispatchQueue.main.async {
                        self.userReviews = fetched.map {
                            GoogleReviewUI(
                                rating: $0.rating,
                                author: $0.authorName,
                                text: $0.comment
                            )
                        }
                    }
                }
            }
        }
        .sheet(isPresented: $isAddingReview) {
            AddReviewView(isPresented: $isAddingReview) { newReview in
                userReviews.append(newReview)
                
                reviewManager.addReview(
                    for: restaurant.placeId,
                    userId: session.uid,
                    authorName: session.email, // או firstName אם יש לך
                    rating: newReview.rating,
                    comment: newReview.text
                )
            }
        }
    }
    
    private func deleteReview(reviewId: String) {
        userReviews.removeAll { $0.id == reviewId }

        reviewManager.deleteReview(
            for: restaurant.placeId,
            userId: session.uid,
            reviewId: reviewId
        )
    }

}
