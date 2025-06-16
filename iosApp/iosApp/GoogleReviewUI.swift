import Foundation
import Shared

struct GoogleReviewUI: Identifiable {
    let id = UUID()
    let rating: Double
    let author: String
    let text: String

    init(rating: Double, author: String, text: String) {
        self.rating = rating
        self.author = author
        self.text = text
    }

    init(from shared: GoogleReview) {
        self.rating = Double(shared.rating)
        self.author = shared.author_name
        self.text = shared.text
    }
}
