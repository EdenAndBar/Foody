import SwiftUI
import Shared

struct BottomSheetView: View {
    let restaurant: Restaurant
    @Binding var favorites: [Restaurant]
    @Environment(\.dismiss) var dismiss
    @StateObject private var viewModel = RestaurantDetailsViewModel()
    
    // ביקורות שנוספו ידנית (לא מה־API)
    @State private var userReviews: [GoogleReviewUI] = []
    @State private var isAddingReview = false
    @State private var newRating: Double = 5.0
    @State private var newText: String = ""
    @State private var selectedReviewID: UUID? = nil
    
    private func toggleFavorite() {
        if let index = favorites.firstIndex(of: restaurant) {
            favorites.remove(at: index)
        } else {
            favorites.append(restaurant)
        }
    }

    var isFavorite: Bool {
        favorites.contains(restaurant)
    }

    // כל הביקורות: מה-API + מהמשתמש
    var allReviews: [GoogleReviewUI] {
        viewModel.googleReviews.map { GoogleReviewUI(from: $0) } + userReviews
    }

    var body: some View {
        VStack(spacing: 20) {
            Text(restaurant.name)
                .font(.title2)
                .bold()

            AsyncImage(url: URL(string: restaurant.photoUrl)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                ProgressView()
            }
            .frame(height: 150)
            .clipShape(RoundedRectangle(cornerRadius: 12))

            // ⭐️ דירוג
            HStack {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                Text(String(format: "%.1f", restaurant.rating))
                    .font(.subheadline)
            }

            // 📝 תיאור
            if !restaurant.address.isEmpty {
                Text(restaurant.address)
                    .font(.body)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }

            HStack(spacing: 40) {
                // ❤️ Favorite
                VStack {
                    Button(action: {
                        toggleFavorite()
                    }) {
                        Image(systemName: isFavorite ? "heart.fill" : "heart")
                            .font(.system(size: 24))
                            .foregroundColor(.pink)
                    }
                    Text("Favorite")
                        .font(.footnote)
                        .foregroundColor(.pink)
                }

                // ✍️ Add Review
                VStack {
                    Button(action: {
                        isAddingReview = true
                    }) {
                        Image(systemName: "pencil")
                            .font(.system(size: 24))
                            .foregroundColor(.green)
                    }
                    Text("Add review")
                        .font(.footnote)
                        .foregroundColor(.green)
                }

                // 🌐 Website
                VStack {
                    Button(action: {
                        if let url = URL(string: viewModel.googleMapsURL) {
                            UIApplication.shared.open(url)
                        }
                    }) {
                        Image(systemName: "safari")
                            .font(.system(size: 24))
                            .foregroundColor(.blue)
                    }
                    Text("Website")
                        .font(.footnote)
                        .foregroundColor(.blue)
                }
            }
            .padding(.top, 10)

            // ⭐️ הצגת כל הביקורות
            if !allReviews.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 16) {
                        ForEach(allReviews) { review in
                                        VStack(alignment: .leading, spacing: 4) {
                                            HStack {
                                                Image(systemName: "star.fill")
                                                    .foregroundColor(.yellow)
                                                Text(String(format: "%.1f", review.rating))
                                                    .font(.subheadline)
                                            }

                                            Text(review.text)
                                                .font(.footnote)
                                                .foregroundColor(.gray)
                                                .lineLimit(3)
                                                .frame(width: 200, alignment: .leading)
                                        }
                                        .padding()
                                        .background(Color(.systemGray6))
                                        .cornerRadius(10)
                                    }
                    }
                    .padding(.horizontal)
                }
            }

            Spacer()
        }
        .padding()
        .presentationDetents([.fraction(0.70)])
        .onAppear {
            Task {
                await viewModel.fetchDetails(for: restaurant.placeId)
            }
        }
        .sheet(isPresented: $isAddingReview) {
            VStack(spacing: 16) {
                Text("Add Your Review")
                    .font(.headline)

                VStack {
                    Text("Rating: \(Int(newRating))")
                    Slider(value: $newRating, in: 1...5, step: 1)
                }

                TextField("Write your review...", text: $newText, axis: .vertical)
                    .textFieldStyle(.roundedBorder)
                    .padding()

                Button("Submit") {
                    let newReview = GoogleReviewUI(
                        rating: newRating,
                        author: "You",
                        text: newText
                    )
                    userReviews.append(newReview)
                    isAddingReview = false
                    newText = ""
                    newRating = 5.0
                }
                .buttonStyle(.borderedProminent)

                Button("Cancel", role: .cancel) {
                    isAddingReview = false
                }
            }
            .padding()
        }
    }
}

