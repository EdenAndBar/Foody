import Shared

class RestaurantApi {
    func getRestaurants(location: String, callback: @escaping ([Restaurant]) -> Void) {
        RestaurantApiService().getRestaurants(location: location, callback: callback)
    }

    func getRestaurants(city: String, callback: @escaping ([Restaurant]) -> Void) {
        RestaurantApiService().getRestaurantsByCity(city: city, callback: callback)
    }
}

