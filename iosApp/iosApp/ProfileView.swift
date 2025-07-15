import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct ProfileView: View {
    @State private var showProfile = false
    @Binding var showSidebar: Bool
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
        ZStack {
            Image("backgroundImage")
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
                .opacity(1.0)
                .offset(x: -20)
                .ignoresSafeArea()

            VStack {
                Spacer().frame(height: 350)

                VStack(spacing: 20) {


                    IconTextField(iconName: "person", placeholder: "First Name", text: $firstName)
                    IconTextField(iconName: "person", placeholder: "Last Name", text: $lastName)
                    IconTextField(iconName: "at", placeholder: "Email", text: .constant(email))
                        .disabled(true)

                    if let error = errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }

                    if let success = successMessage {
                        Text(success)
                            .foregroundColor(.green)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }

                    Button(action: saveChanges) {
                        Text("Save Changes")
                            .foregroundColor(.white)
                            .frame(width: 180, height: 44)
                            .background(Color.green.opacity(0.8))
                            .cornerRadius(20)
                    }

                    Button(action: {
                        showPasswordAlert = true
                    }) {
                        Text("Update Password")
                            .foregroundColor(.white)
                            .frame(width: 180, height: 44)
                            .background(Color.orange.opacity(0.8))
                            .cornerRadius(20)
                    }
                }

                Spacer()
            }
            .padding()
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: {
                        showSidebar = true
                        dismiss()
                    }) {
                        HStack {
                            Image(systemName: "chevron.backward")
                            Text("Back")
                        }
                    }
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .onAppear(perform: loadUserData)
        .onDisappear {
            showSidebar = true
        }
        .alert("Update Password", isPresented: $showPasswordAlert, actions: {
            SecureField("Current Password", text: $currentPassword)
            SecureField("New Password", text: $newPassword)
            Button("Update", action: updatePassword)
            Button("Cancel", role: .cancel) {}
        }, message: {
            Text("Enter your current and new password")
        })
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
