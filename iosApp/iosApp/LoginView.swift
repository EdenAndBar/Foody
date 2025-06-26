//import SwiftUI
//import FirebaseAuth
//import GoogleSignIn
//import FirebaseCore
//
//struct LoginView: View {
//    @State private var email = ""
//    @State private var password = ""
//    @State private var errorMessage: String?
//    @State private var isLogin = true
//    @Binding var isLoggedIn: Bool
//    var onLoginSuccess: () -> Void
//
//    @Environment(\.dismiss) var dismiss
//
//    var body: some View {
//        VStack(spacing: 20) {
//            Text(isLogin ? "Login" : "Sign Up")
//                .font(.largeTitle)
//                .bold()
//
//            TextField("Email", text: $email)
//                .textFieldStyle(RoundedBorderTextFieldStyle())
//                .keyboardType(.emailAddress)
//                .autocapitalization(.none)
//
//            SecureField("Password", text: $password)
//                .textFieldStyle(RoundedBorderTextFieldStyle())
//
//            if let error = errorMessage {
//                Text(error)
//                    .foregroundColor(.red)
//                    .multilineTextAlignment(.center)
//            }
//
//            Button(action: handleAuth) {
//                Text(isLogin ? "Login" : "Sign Up")
//                    .frame(maxWidth: .infinity)
//                    .padding()
//                    .background(Color.blue)
//                    .foregroundColor(.white)
//                    .cornerRadius(12)
//            }
//
//            Button(action: signInWithGoogle) {
//                HStack {
//                    Image(systemName: "globe")
//                    Text("Sign in with Google").bold()
//                }
//                .frame(maxWidth: .infinity)
//                .padding()
//                .background(Color.red)
//                .foregroundColor(.white)
//                .cornerRadius(12)
//            }
//
//            Button(action: {
//                isLogin.toggle()
//                errorMessage = nil
//            }) {
//                Text(isLogin ? "Don't have an account? Sign Up" : "Already have an account? Login")
//                    .font(.caption)
//            }
//        }
//        .padding()
//    }
//
//    private func handleAuth() {
//        errorMessage = nil
//        if isLogin {
//            Auth.auth().signIn(withEmail: email, password: password) { result, error in
//                if let error = error {
//                    errorMessage = error.localizedDescription
//                } else {
//                    onLoginSuccess()
//                }
//            }
//
//        } else {
//            Auth.auth().createUser(withEmail: email, password: password) { result, error in
//                if let error = error {
//                    errorMessage = error.localizedDescription
//                } else {
//                    onLoginSuccess()
//                }
//            }
//        }
//    }
//
//    func signInWithGoogle() {
//        guard let presentingVC = UIApplication.shared.connectedScenes
//                .compactMap({ ($0 as? UIWindowScene)?.keyWindow?.rootViewController })
//                .first else {
//            self.errorMessage = "Unable to access root view controller"
//            return
//        }
//
//        Task {
//            do {
//                let signInResult = try await GIDSignIn.sharedInstance.signIn(withPresenting: presentingVC)
//
//                let user = signInResult.user
//                guard let idToken = user.idToken?.tokenString else {
//                    self.errorMessage = "Missing ID token"
//                    return
//                }
//
//                let accessToken = user.accessToken.tokenString
//                let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: accessToken)
//
//                Auth.auth().signIn(with: credential) { result, error in
//                    if let error = error {
//                        self.errorMessage = error.localizedDescription
//                    } else {
//                        self.isLoggedIn = true
//                        self.onLoginSuccess()
//                    }
//                }
//
//            } catch {
//                self.errorMessage = error.localizedDescription
//            }
//        }
//    }
//}

import SwiftUI
import FirebaseAuth
import GoogleSignIn
import FirebaseCore

struct LoginView: View {
    @State private var email = ""
    @State private var password = ""
    @State private var errorMessage: String?
    @State private var showSignup = false

    @Binding var isLoggedIn: Bool
    var onLoginSuccess: () -> Void

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                Text("Login")
                    .font(.largeTitle)
                    .bold()

                TextField("Email", text: $email)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)

                SecureField("Password", text: $password)
                    .textFieldStyle(RoundedBorderTextFieldStyle())

                if let error = errorMessage {
                    Text(error)
                        .foregroundColor(.red)
                        .multilineTextAlignment(.center)
                }

                Button(action: handleLogin) {
                    Text("Login")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                }

                Button(action: signInWithGoogle) {
                    HStack(spacing: 10) {
                        Image("google_logo")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 18, height: 18)
                        
                        Text("Sign in with Google")
                            .font(.system(size: 16, weight: .semibold))
                    }
                    .frame(maxWidth: .infinity, minHeight: 44)
                    .padding(.horizontal)
                    .background(Color.white)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                    )
                    .foregroundColor(.black)
                    .cornerRadius(12)
                }

                // מעבר למסך הרשמה
                Button(action: {
                    showSignup = true
                }) {
                    
                    Text("Don't have an account? Sign up")
                        .font(.caption)
                }

                // ניווט למסך Signup
                NavigationLink(destination: SignupView(isLoggedIn: $isLoggedIn, onSignupSuccess: onLoginSuccess), isActive: $showSignup) {
                    EmptyView()
                }
            }
            .padding()
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

