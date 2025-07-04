//import Shared
//import SwiftUI
//
//struct IdentifiableRestaurant: Identifiable, Hashable {
//    let restaurant: Restaurant
//    var id: String { restaurant.placeId }
//    var guessedCategory: String
//}


import Shared
import SwiftUI

struct IdentifiableRestaurant: Identifiable, Hashable {
    let restaurant: Restaurant
    var guessedCategory: String

    var id: String {
        restaurant.placeId
    }

    static func == (lhs: IdentifiableRestaurant, rhs: IdentifiableRestaurant) -> Bool {
        lhs.id == rhs.id
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
}
