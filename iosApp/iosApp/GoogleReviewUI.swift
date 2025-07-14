import Foundation
import Shared

struct GoogleReviewUI: Identifiable {
    let id: String  // מזהה Firebase
    let rating: Double
    let author: String
    let text: String
    let uid: String?

    init(id: String = UUID().uuidString, rating: Double, author: String, text: String, uid: String? = nil) {
        self.id = id
        self.rating = rating
        self.author = author
        self.text = text
        self.uid = uid
    }

    init(from shared: GoogleReview) {
        self.id = "\(shared.author_name)-\(shared.text)"
        self.rating = Double(shared.rating)
        self.author = shared.author_name
        self.text = shared.text
        self.uid = nil
    }
}

