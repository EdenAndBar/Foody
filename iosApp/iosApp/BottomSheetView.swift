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
    @State private var isOpeningHoursExpanded = false
    
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
            // שם ודירוג באותה שורה, מיושרים במרכז
            HStack(spacing: 8) {
                Text(restaurant.name)
                    .font(.title2)
                    .bold()

                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                
                Text(String(format: "%.1f", restaurant.rating))
                    .font(.subheadline)
                    .foregroundColor(.primary)
            }
            .frame(maxWidth: .infinity)
            .multilineTextAlignment(.center)
            .padding(.horizontal)

            // כתובת מתחת לשם
            if !restaurant.address.isEmpty {
                Text(restaurant.address)
                    .font(.body)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }

            // התמונה
            AsyncImage(url: URL(string: restaurant.photoUrl)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                ProgressView()
            }
            .frame(height: 150)
            .clipShape(RoundedRectangle(cornerRadius: 12))

            // שורה שמציגה את מצב הפתיחה (Open Now/Closed) מצד שמאל
            HStack {
                Text(isOpen ? "Open Now" : "Closed")
                    .padding(6)
                    .background(isOpen ? Color.green.opacity(0.2) : Color.red.opacity(0.2))
                    .foregroundColor(isOpen ? .green : .red)
                    .cornerRadius(8)
                    .font(.subheadline)
                    .bold()
                    .frame(maxWidth: .infinity, alignment: .leading)  // יישור לשמאל

                Spacer()

                // כפתור לפתיחת שעות הפתיחה מצד ימין
                Button(action: {
                    withAnimation {
                        isOpeningHoursExpanded.toggle()
                    }
                }) {
                    HStack(spacing: 2) {
                        Text("Opening Hours")
                            .font(.headline)
                            .foregroundColor(.blue)
                        Image(systemName: isOpeningHoursExpanded ? "chevron.up" : "chevron.down")
                            .foregroundColor(.blue)
                    }
                }
            }
            .padding(.horizontal)

            // גלילה של שעות הפתיחה מיושרת לצד ימין
            if isOpeningHoursExpanded, let openingHours = restaurant.openingHoursText {
                ScrollView(.vertical) {
                    VStack(alignment: .trailing, spacing: 4) {
                        ForEach(openingHours, id: \.self) { hourText in
                            Text(hourText)
                                .font(.subheadline)
                                .bold()
                                .padding(.trailing)
                                .frame(maxWidth: .infinity, alignment: .trailing)  // יישור לימין
                        }
                    }
                    .padding(.vertical)
                    .padding(.trailing)
                }
                // תשאירי את הסביבה עם layoutDirection ל-RTL כדי ליישר לימין
                .environment(\.layoutDirection, .leftToRight)
                .frame(maxHeight: 120)
            }

            // שאר התוכן (כפתורי פעולה, ביקורות וכו')
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
