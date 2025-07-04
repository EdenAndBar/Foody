import Foundation
import Combine

class RestaurantFilter: ObservableObject {
    enum SortDirection {
        case ascending, descending
    }

    enum SortField {
        case name, rating
    }

    @Published var sortField: SortField = .name
    @Published var sortDirection: SortDirection = .ascending

    @Published var onlyOpen: Bool = false
    @Published var ratingRange: ClosedRange<Double> = 0...5

    func apply(to restaurants: [Restaurant]) -> [Restaurant] {
        var filtered = restaurants

        // סינון לפי פתוחים עכשיו
        if onlyOpen {
            filtered = filtered.filter { $0.isOpenNow == true }
        }

        // סינון לפי טווח דירוג
        filtered = filtered.filter {
            let rating = Double($0.rating)
            return ratingRange.contains(rating)
        }

        // מיון לפי שדה וכיוון
        switch (sortField, sortDirection) {
        case (.name, .ascending):
            filtered = filtered.sorted { $0.name.lowercased() < $1.name.lowercased() }
        case (.name, .descending):
            filtered = filtered.sorted { $0.name.lowercased() > $1.name.lowercased() }
        case (.rating, .ascending):
            filtered = filtered.sorted { $0.rating < $1.rating }
        case (.rating, .descending):
            filtered = filtered.sorted { $0.rating > $1.rating }
        }

        return filtered
    }
}
