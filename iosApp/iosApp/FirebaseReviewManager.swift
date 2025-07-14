import Foundation
import FirebaseFirestore

struct Review {
    let userId: String
    let authorName: String
    let rating: Double
    let comment: String
    let timestamp: Date
}

class FirebaseReviewManager {
    private let db = Firestore.firestore()

    func addReview(for placeId: String, userId: String, authorName: String, rating: Double, comment: String) {
        let reviewData: [String: Any] = [
            "userId": userId,
            "authorName": authorName,
            "rating": rating,
            "comment": comment,
            "timestamp": Timestamp(date: Date())
        ]
        
        db.collection("restaurants")
          .document(placeId)
          .collection("reviews")
    }

    func deleteReview(for placeId: String, userId: String, reviewId: String) {
        let db = Firestore.firestore()
        let docRef = db.collection("restaurants")
            .document(placeId)
            .collection("reviews")
            .document(reviewId)

        docRef.delete { error in
            if let error = error {
                print("Error deleting review: \(error)")
            } else {
                print("Review deleted successfully.")
            }
        }
    }

    func fetchReviews(for placeId: String, completion: @escaping ([Review]) -> Void) {
        db.collection("restaurants")
            .document(placeId)
            .collection("reviews")
            .order(by: "timestamp", descending: true)
            .getDocuments { snapshot, error in
                if let documents = snapshot?.documents {
                    let reviews: [Review] = documents.compactMap { doc in
                        let data = doc.data()
                        return Review(
                            userId: data["userId"] as? String ?? "",
                            authorName: data["authorName"] as? String ?? "Anonymous",
                            rating: data["rating"] as? Double ?? 0.0,
                            comment: data["comment"] as? String ?? "",
                            timestamp: (data["timestamp"] as? Timestamp)?.dateValue() ?? Date()
                        )
                    }
                    completion(reviews)
                } else {
                    completion([])
                }
            }
    }
}
