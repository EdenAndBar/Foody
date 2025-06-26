import SwiftUI
import FirebaseAuth

struct ProfileView: View {
    @Binding var isLoggedIn: Bool
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                // תמונת פרופיל (בעתיד אפשר לאפשר העלאה)
                Image(systemName: "person.crop.circle.fill")
                    .resizable()
                    .frame(width: 100, height: 100)
                    .foregroundColor(.gray)

                // שם משתמש - אפשר להרחיב בעתיד
                Text("Your Name")
                    .font(.title2)
                    .bold()

                Button("Update Password") {
                    // אפשרות לעדכון בעתיד
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.orange)
                .foregroundColor(.white)
                .cornerRadius(12)

                Button("Log Out") {
                    try? Auth.auth().signOut()
                    isLoggedIn = false
                    dismiss()
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.red)
                .foregroundColor(.white)
                .cornerRadius(12)

                Spacer()
            }
            .padding()
            .navigationTitle("Profile")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
