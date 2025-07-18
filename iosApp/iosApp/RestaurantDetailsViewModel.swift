import Foundation
import Shared

@MainActor
class RestaurantDetailsViewModel: ObservableObject {
    @Published var googleReviews: [GoogleReview] = []
    @Published var userReviews: [GoogleReviewUI] = []
    @Published var googleMapsURL: String = ""
    @Published var websiteURL: String = ""

    func fetchDetails(for placeId: String) async {
        let api = RestaurantApiService()
        api.getRestaurantDetails(placeId: placeId) { details in
            if let details = details {
                self.googleReviews = details.reviews ?? []
                self.googleMapsURL = details.url ?? ""
                self.websiteURL = details.website ?? ""
            }
        }
    }

    var allReviews: [GoogleReviewUI] {
        googleReviews.map { GoogleReviewUI(from: $0) } + userReviews
    }
}
