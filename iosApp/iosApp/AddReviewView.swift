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
        VStack(spacing: 16) {
            Text("Add Your Review")
                .font(.headline)

            TextField("Your name", text: $newAuthor)
                .textFieldStyle(.roundedBorder)
                .padding(.horizontal)

            VStack {
                Text("Rating: \(Int(newRating))")
                Slider(value: $newRating, in: 1...5, step: 1)
            }

            TextField("Write your review...", text: $newText, axis: .vertical)
                .textFieldStyle(.roundedBorder)
                .padding()

            Button("Submit") {
                let review = GoogleReviewUI(
                    rating: newRating,
                    author: newAuthor.isEmpty ? "Anonymous" : newAuthor,
                    text: newText
                )
                onSubmit(review)
                isPresented = false
                newAuthor = ""
                newText = ""
                newRating = 5.0
            }
            .buttonStyle(.borderedProminent)

            Button("Cancel", role: .cancel) {
                isPresented = false
            }
        }
        .padding()
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
