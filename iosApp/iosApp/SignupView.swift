import SwiftUI
import FirebaseAuth
import PhotosUI

struct SignupView: View {
    @Binding var isLoggedIn: Bool
    var onSignupSuccess: () -> Void

    @State private var firstName = ""
    @State private var lastName = ""
    @State private var email = ""
    @State private var password = ""
    @State private var image: UIImage?
    @State private var selectedItem: PhotosPickerItem?
    @State private var errorMessage: String?

    var body: some View {
        VStack(spacing: 20) {
            Text("Sign Up")
                .font(.largeTitle)
                .bold()
            
            PhotosPicker(selection: $selectedItem, matching: .images) {
                ZStack {
                    if let img = image {
                        Image(uiImage: img)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 100, height: 100)
                            .clipShape(Circle())
                            .overlay(Circle().stroke(Color.gray, lineWidth: 2))
                    } else {
                        Circle()
                            .fill(Color.gray.opacity(0.2))
                            .frame(width: 100, height: 100)
                            .overlay(
                                Image(systemName: "camera.fill")
                                    .font(.system(size: 30))
                                    .foregroundColor(.gray)
                                    .background(Circle().fill(Color.white).frame(width: 35, height: 35))
                                    .offset(x: 30, y: 30)
                            )
                    }
                }
            }
            .buttonStyle(.plain)

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

            .onChange(of: selectedItem) { newItem in
                Task {
                    if let data = try? await newItem?.loadTransferable(type: Data.self),
                       let uiImage = UIImage(data: data) {
                        self.image = uiImage
                    }
                }
            }

            if let img = image {
                Image(uiImage: img)
                    .resizable()
                    .scaledToFit()
                    .frame(height: 100)
                    .clipShape(Circle())
            }

            if let error = errorMessage {
                Text(error).foregroundColor(.red)
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
                errorMessage = error.localizedDescription
            } else {
                // אופציונלי: שמירה של firstName, lastName ו־image במקום אחר (Firestore/Storage)
                onSignupSuccess()
                isLoggedIn = true
            }
        }
    }
}
