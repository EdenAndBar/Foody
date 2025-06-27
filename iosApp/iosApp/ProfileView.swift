import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct ProfileView: View {
    @Binding var isLoggedIn: Bool
    @Environment(\.dismiss) var dismiss

    @State private var firstName = ""
    @State private var lastName = ""
    @State private var email = ""
    @State private var isLoading = true
    @State private var errorMessage: String?
    @State private var successMessage: String?
    @State private var showPasswordAlert = false
    @State private var newPassword = ""
    @State private var currentPassword = ""

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                if isLoading {
                    ProgressView("Loading...")
                } else {
                    Text("Edit Profile")
                        .font(.largeTitle)
                        .bold()

                    TextField("First Name", text: $firstName)
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                    TextField("Last Name", text: $lastName)
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                    TextField("Email", text: .constant(email))
                        .disabled(true)
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                    Button("Save Changes", action: saveChanges)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(12)

                    Button("Update Password") {
                        showPasswordAlert = true
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

                    if let error = errorMessage {
                        Text(error).foregroundColor(.red)
                    }

                    if let success = successMessage {
                        Text(success).foregroundColor(.green)
                    }

                    Spacer()
                }
            }
            .padding()
            .navigationTitle("Profile")
            .navigationBarTitleDisplayMode(.inline)
            .onAppear(perform: loadUserData)
            .alert("Update Password", isPresented: $showPasswordAlert, actions: {
                SecureField("Current Password", text: $currentPassword)
                SecureField("New Password", text: $newPassword)
                Button("Update", action: updatePassword)
                Button("Cancel", role: .cancel) {}
            }, message: {
                Text("Enter your current and new password")
            })
        }
    }

    func loadUserData() {
        guard let user = Auth.auth().currentUser else { return }
        email = user.email ?? ""

        let db = Firestore.firestore()
        db.collection("users").document(user.uid).getDocument { doc, error in
            if let data = doc?.data() {
                self.firstName = data["firstName"] as? String ?? ""
                self.lastName = data["lastName"] as? String ?? ""
            }
            self.isLoading = false
        }
    }

    func saveChanges() {
        guard let user = Auth.auth().currentUser else { return }

        let db = Firestore.firestore()
        let updates: [String: Any] = [
            "firstName": firstName,
            "lastName": lastName
        ]

        db.collection("users").document(user.uid).setData(updates, merge: true)
    }

    func updatePassword() {
        errorMessage = nil
        successMessage = nil

        guard !currentPassword.isEmpty, !newPassword.isEmpty else {
            errorMessage = "Please fill in both fields"
            return
        }

        guard let user = Auth.auth().currentUser,
              let email = user.email else {
            errorMessage = "No authenticated user"
            return
        }

        let credential = EmailAuthProvider.credential(withEmail: email, password: currentPassword)

        user.reauthenticate(with: credential) { _, error in
            if let error = error {
                errorMessage = "Current password is incorrect: \(error.localizedDescription)"
            } else {
                user.updatePassword(to: newPassword) { error in
                    if let error = error {
                        errorMessage = "Failed to update password: \(error.localizedDescription)"
                    } else {
                        successMessage = "Password updated successfully"
                        currentPassword = ""
                        newPassword = ""
                    }
                }
            }
        }
    }
}
