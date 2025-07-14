import Foundation
import FirebaseFirestore
import Shared

class FirebaseFavoritesManager {
    private let db = Firestore.firestore()
    
    func addFavorite(for userId: String, restaurant: Restaurant) {
        let data: [String: Any] = [
            "placeId": restaurant.placeId,
            "name": restaurant.name,
            "address": restaurant.address,
            "rating": restaurant.rating,
            "photoUrl": restaurant.photoUrl,
            "category": restaurant.category
        ]
        
        db.collection("users").document(userId).collection("favorites")
            .document(restaurant.placeId)
            .setData(data)
    }
    
    func removeFavorite(for userId: String, restaurant: Restaurant) {
        db.collection("users").document(userId).collection("favorites")
            .document(restaurant.placeId)
            .delete()
    }
    
    func fetchFavorites(for userId: String, completion: @escaping ([Restaurant]) -> Void) {
        db.collection("users").document(userId).collection("favorites")
            .getDocuments(completion: { snapshot, error in
                if let documents = snapshot?.documents {
                    let favorites = documents.compactMap { doc -> Restaurant? in
                        let data = doc.data()
                        return Restaurant(
                            id: doc.documentID,
                            placeId: data["placeId"] as? String ?? "",
                            name: data["name"] as? String ?? "",
                            photoUrl: data["photoUrl"] as? String ?? "",
                            address: data["address"] as? String ?? "",
                            rating: (data["rating"] as? NSNumber)?.floatValue ?? 0.0,
                            types: data["types"] as? [String] ?? [],
                            isOpenNow: (data["isOpenNow"] as? Bool).map { KotlinBoolean(bool: $0) },
                            openingHoursText: data["openingHoursText"] as? [String],
                            category: data["category"] as? String ?? "",
                            phoneNumber: data["phoneNumber"] as? String
                        )
                    }
                    completion(favorites)
                } else {
                    completion([])
                }
            })
    }
}
