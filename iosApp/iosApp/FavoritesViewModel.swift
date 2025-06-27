import Foundation
import Shared
import Combine

@MainActor
class FavoritesViewModel: ObservableObject {
    @Published var favorites: [Restaurant] = []
    
    private let favoritesRepository = FavoritesRepository()
    
    init() {
        loadFavorites()
    }
    
    func loadFavorites() {
        favorites = favoritesRepository.getFavorites() as? [Restaurant] ?? []
    }
    
    func addFavorite(_ restaurant: Restaurant) {
        favoritesRepository.addFavorite(restaurant: restaurant)
        favorites.append(restaurant)
    }

    func removeFavorite(_ restaurant: Restaurant) {
        favoritesRepository.removeFavorite(restaurant: restaurant)
        favorites.removeAll { $0 == restaurant }
    }

    func isFavorite(_ restaurant: Restaurant) -> Bool {
        return favoritesRepository.isFavorite(restaurant: restaurant)
    }
}
