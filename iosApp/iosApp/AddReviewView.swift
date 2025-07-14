import SwiftUI
import FirebaseAuth
import FirebaseFirestore

struct AddReviewView: View {
    @Binding var isPresented: Bool
    var onSubmit: (GoogleReviewUI) -> Void

    @State private var newAuthor: String = ""
    @State private var newRating: Double = 5.0
    @State private var newText: String = ""
    @State private var fullName: String = ""

    var body: some View {
        VStack(spacing: 20) {
            Text("Add Your Review")
                .font(.title2)
                .bold()
                .foregroundColor(.primary)

            IconTextField(iconName: "person", placeholder: "Your name", text: $newAuthor)

            VStack(spacing: 8) {
                Text("Rating: \(Int(newRating))")
                    .font(.subheadline)
                    .foregroundColor(.gray)

                Slider(value: $newRating, in: 1...5, step: 1)
                    .accentColor(.blue)
                    .padding(.horizontal)
            }

            ZStack(alignment: .topLeading) {
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                    .background(Color.white.opacity(0.2))
                    .cornerRadius(20)

                TextEditor(text: $newText)
                    .padding(12)
                    .foregroundColor(.primary)
                    .background(Color.clear)
                    .frame(height: 120)
            }
            .padding(.horizontal)
            .frame(maxWidth: 350)

            Button(action: {
                let review = GoogleReviewUI(
                    rating: newRating,
                    author: newAuthor.isEmpty ? "Anonymous" : newAuthor,
                    text: newText,
                    uid: Auth.auth().currentUser?.uid
                )
                onSubmit(review)
                isPresented = false
                newAuthor = ""
                newText = ""
                newRating = 5.0
            }) {
                Text("Submit")
                    .frame(width: 150, height: 44)
                    .background(newText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? Color.gray : Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(20)
            }
            .disabled(newText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)

            Button("Cancel", role: .cancel) {
                isPresented = false
            }
            .foregroundColor(.red)
        }
        .padding(.top, 40)
        .onAppear(perform: loadUserName)
    }

    func loadUserName() {
        guard let uid = Auth.auth().currentUser?.uid else { return }

        let db = Firestore.firestore()
        db.collection("users").document(uid).getDocument { doc, error in
            if let data = doc?.data() {
                let first = data["firstName"] as? String ?? ""
                let last = data["lastName"] as? String ?? ""
                fullName = "\(first) \(last)".trimmingCharacters(in: .whitespaces)
                if newAuthor.isEmpty {
                    newAuthor = fullName
                }
            }
        }
    }
}

