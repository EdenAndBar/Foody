import Shared
import SwiftUI

struct IdentifiableRestaurant: Identifiable, Hashable {
    let restaurant: Restaurant
    var id: String { restaurant.placeId }
    var guessedCategory: String
}
