import SwiftUI
import FirebaseAuth

struct SettingsView: View {
    @Binding var isLoggedIn: Bool

    var body: some View {
        VStack(spacing: 20) {
            Text("Settings")
                .font(.title)

            Button("Sign Out") {
                do {
                    try Auth.auth().signOut()
                    isLoggedIn = false
                } catch {
                    print("Sign out failed: \(error.localizedDescription)")
                }
            }
            .foregroundColor(.red)
            .padding()
        }
        .padding()
    }
}
