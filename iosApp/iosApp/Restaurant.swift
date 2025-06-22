import Shared

struct IdentifiableRestaurant: Identifiable, Hashable {
    let restaurant: Restaurant
    var id: String { restaurant.placeId }
}
