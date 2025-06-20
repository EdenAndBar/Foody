import Shared

class RestaurantApi {
    func getRestaurants(location: String, callback: @escaping ([Restaurant]) -> Void) {
        Shared.RestaurantApi().getRestaurants(location: location, callback: callback)
    }

    func getRestaurants(city: String, callback: @escaping ([Restaurant]) -> Void) {
        Shared.RestaurantApi().getRestaurantsByCity(city: city, callback: callback)
    }
}

