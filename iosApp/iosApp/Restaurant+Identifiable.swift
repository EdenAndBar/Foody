import Shared

struct IdentifiableRestaurant: Identifiable {
    let restaurant: Restaurant
    var id: String { restaurant.placeId }
}
