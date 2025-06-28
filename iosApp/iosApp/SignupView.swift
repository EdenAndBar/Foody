import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct SignupView: View {
    @Binding var isLoggedIn: Bool
    var onSignupSuccess: () -> Void

    @State private var firstName = ""
    @State private var lastName = ""
    @State private var email = ""
    @State private var password = ""
    @State private var errorMessage: String?

    var body: some View {
        VStack(spacing: 20) {
            Text("Sign Up")
                .font(.largeTitle)
                .bold()

            TextField("First Name", text: $firstName)
                .textFieldStyle(RoundedBorderTextFieldStyle())

            TextField("Last Name", text: $lastName)
                .textFieldStyle(RoundedBorderTextFieldStyle())

            TextField("Email", text: $email)
                .keyboardType(.emailAddress)
                .autocapitalization(.none)
                .textFieldStyle(RoundedBorderTextFieldStyle())

            SecureField("Password", text: $password)
                .textFieldStyle(RoundedBorderTextFieldStyle())

            if let error = errorMessage {
                Text(error)
                    .foregroundColor(.red)
            }

            Button(action: registerUser) {
                Text("Create Account")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(12)
            }
        }
        .padding()
    }

    func registerUser() {
        errorMessage = nil

        Auth.auth().createUser(withEmail: email, password: password) { result, error in
            if let error = error {
                errorMessage = "Signup failed: \(error.localizedDescription)"
            } else if let user = result?.user {
                let db = Firestore.firestore()
                let userDoc = db.collection("users").document(user.uid)

                let data: [String: Any] = [
                    "firstName": firstName,
                    "lastName": lastName,
                    "email": email
                ]

                userDoc.setData(data) { error in
                    if let error = error {
                        errorMessage = "Failed saving user data: \(error.localizedDescription)"
                    } else {
                        onSignupSuccess()
                        isLoggedIn = true
                    }
                }
            }
        }
    }
}
