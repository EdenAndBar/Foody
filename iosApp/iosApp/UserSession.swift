import Foundation
import FirebaseAuth

class UserSession: ObservableObject {
    @Published var uid: String = ""
    @Published var email: String = ""
    @Published var favorites: [Restaurant] = []

    func updateFromFirebaseUser(_ user: User) {
        self.uid = user.uid
        self.email = user.email ?? ""
    }

    func clear() {
        uid = ""
        email = ""
        favorites = []
    }
}
