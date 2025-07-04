import Foundation
import Shared

class MapViewModel: ObservableObject {
    @Published var restaurants: [Restaurant] = []
}
