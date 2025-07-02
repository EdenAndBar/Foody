import SwiftUI
import FirebaseAuth
import GoogleSignIn
import FirebaseCore

struct IconTextField: View {
    var iconName: String
    var placeholder: String
    @Binding var text: String
    var isSecure: Bool = false

    var body: some View {
        HStack {
            Image(systemName: iconName)
                .foregroundColor(.gray)
                .padding(.leading, 12)

            if isSecure {
                SecureField(placeholder, text: $text)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .padding(.vertical, 12)
            } else {
                TextField(placeholder, text: $text)
                    .keyboardType(placeholder == "Email" ? .emailAddress : .default)
                    .autocapitalization(.none)
                    .disableAutocorrection(true)
                    .padding(.vertical, 12)
            }
        }
        .background(Color.white.opacity(0.2))
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .stroke(Color.gray.opacity(0.5), lineWidth: 1)
        )
        .cornerRadius(20)
        .padding(.horizontal)
        .foregroundColor(.primary)
        .frame(maxWidth: 350)
    }
}

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var errorMessage: String?
    @State private var showSignup = false

    @Binding var isLoggedIn: Bool
    var onLoginSuccess: () -> Void

    var body: some View {
        NavigationStack {
            ZStack {
                Image("backgroundImage")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
                    .opacity(1.0)
                    .offset(x: -20)
                    .ignoresSafeArea()

                if showSignup {
                    SignupView(
                        onSignupSuccess: {
                            self.isLoggedIn = true
                            self.onLoginSuccess()
                        },
                        onBackToLogin: {
                            self.showSignup = false
                        },
                        isLoggedIn: $isLoggedIn
                    )

                } else {
                    VStack(spacing: 20) {
                        IconTextField(iconName: "at", placeholder: "Email", text: $email)
                        IconTextField(iconName: "eye.slash", placeholder: "Password", text: $password, isSecure: true)

                        if let error = errorMessage {
                            Text(error)
                                .foregroundColor(.red)
                                .multilineTextAlignment(.center)
                                .padding(.horizontal)
                        }

                        HStack(spacing: 16) {
                            Button(action: handleLogin) {
                                Text("Login")
                                    .frame(width: 130, height: 44)
                                    .padding(.vertical, 4)
                                    .background(email.isEmpty || password.isEmpty ? Color.gray : Color.blue)
                                    .foregroundColor(.white)
                                    .cornerRadius(20)
                            }
                            .disabled(email.isEmpty || password.isEmpty)

                            Button(action: signInWithGoogle) {
                                HStack(spacing: 10) {
                                    Text("Sign in with")
                                            .font(.system(size: 16, weight: .semibold))
                                        
                                        Image("google_logo")
                                            .resizable()
                                            .scaledToFit()
                                            .frame(width: 18, height: 18)
                                }
                                .frame(width: 170, height: 44)
                                .padding(.vertical, 4)
                                .background(Color.white.opacity(0.8))
                                .overlay(
                                    RoundedRectangle(cornerRadius: 20)
                                        .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                                )
                                .foregroundColor(.black)
                                .cornerRadius(20)
                            }
                        }
                        .padding(.horizontal)

                        Button(action: {
                            showSignup = true
                        }) {
                            Text("Don't have an account? Sign up")
                                .font(.caption)
                                .foregroundColor(.blue)
                        }
                    }
                    .padding(.top, 120)
                }
            }
        }
    }

    private func handleLogin() {
        errorMessage = nil
        Auth.auth().signIn(withEmail: email, password: password) { result, error in
            if let error = error {
                errorMessage = error.localizedDescription
            } else {
                onLoginSuccess()
            }
        }
    }

    private func signInWithGoogle() {
        guard let presentingVC = UIApplication.shared.connectedScenes
            .compactMap({ ($0 as? UIWindowScene)?.keyWindow?.rootViewController })
            .first else {
            self.errorMessage = "Unable to access root view controller"
            return
        }

        Task {
            do {
                let signInResult = try await GIDSignIn.sharedInstance.signIn(withPresenting: presentingVC)
                guard let idToken = signInResult.user.idToken?.tokenString else {
                    self.errorMessage = "Missing ID token"
                    return
                }

                let accessToken = signInResult.user.accessToken.tokenString
                let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)

                Auth.auth().signIn(with: credential) { result, error in
                    if let error = error {
                        self.errorMessage = error.localizedDescription
                    } else {
                        self.isLoggedIn = true
                        self.onLoginSuccess()
                    }
                }

            } catch {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}
