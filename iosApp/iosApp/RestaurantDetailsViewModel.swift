import Foundation
import Shared

@MainActor
class RestaurantDetailsViewModel: ObservableObject {
    @Published var googleReviews: [GoogleReview] = []
    @Published var userReviews: [GoogleReviewUI] = []
    @Published var googleMapsURL: String = ""

    func fetchDetails(for placeId: String) async {
        let api = RestaurantApi()
        api.getRestaurantDetails(placeId: placeId) { details in
            if let details = details {
                self.googleReviews = details.reviews ?? []
                self.googleMapsURL = details.url ?? ""
            }
        }
    }
    
    var allReviews: [GoogleReviewUI] {
        googleReviews.map { GoogleReviewUI(from: $0) } + userReviews
    }
}

