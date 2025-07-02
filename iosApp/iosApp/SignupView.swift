import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct SignupView: View {
    var onSignupSuccess: () -> Void
    var onBackToLogin: () -> Void
    @Binding var isLoggedIn: Bool
    @State private var firstName = ""
    @State private var lastName = ""
    @State private var email = ""
    @State private var password = ""
    @State private var errorMessage: String?
    
    var isFormComplete: Bool {
        !firstName.isEmpty && !lastName.isEmpty && !email.isEmpty && !password.isEmpty
    }

    var body: some View {
        Image("backgroundImage")
            .resizable()
            .aspectRatio(contentMode: .fill)
            .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
            .opacity(1.0)
            .offset(x: -20)
            .ignoresSafeArea()
        ZStack {
            VStack {
                Spacer().frame(height: 350)

                VStack(spacing: 20) {
                    IconTextField(iconName: "person", placeholder: "First Name", text: $firstName)
                    IconTextField(iconName: "person", placeholder: "Last Name", text: $lastName)
                    IconTextField(iconName: "at", placeholder: "Email", text: $email)
                    IconTextField(iconName: "eye.slash", placeholder: "Password", text: $password, isSecure: true)

                    if let error = errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }

                    Button(action: registerUser) {
                        Text("Create Account")
                            .foregroundColor(.white)
                            .frame(width: 180, height: 44)
                            .background(isFormComplete ? Color.blue : Color.gray)
                            .cornerRadius(20)
                    }
                    .disabled(!isFormComplete)
                    .padding(.horizontal)

                    Button(action: {
                        onBackToLogin()
                    }) {
                        Text("Already have an account? Log in")
                            .font(.caption)
                            .foregroundColor(.blue)
                    }
                }

                Spacer()
            }
        }
    }

    func registerUser() {
        errorMessage = nil

        Auth.auth().createUser(withEmail: email, password: password) { result, error in
            if let error = error {
                print("❌ Registration error: \(error.localizedDescription)")
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
                        print("✅ User registered successfully")
                        onSignupSuccess()
                    }
                }
            }
        }
    }
}
