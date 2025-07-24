import SwiftUI

struct ReviewListView: View {
    let reviews: [GoogleReviewUI]
    let sessionUid: String
    let onDelete: (String) -> Void

    var body: some View {
        if !reviews.isEmpty {
            Text("Reviews")
                .font(.headline)
                .frame(maxWidth: .infinity, alignment: .center)
                .padding(.horizontal)

            ScrollView {
                VStack(spacing: 16) {
                    ForEach(reviews) { review in
                        VStack(alignment: .leading, spacing: 16) {
                            HStack {
                                Image(systemName: "star.fill")
                                    .foregroundColor(.yellow)
                                Text(String(format: "%.1f", review.rating))
                                    .font(.subheadline)
                            }

                            Text("by \(review.author)")
                                .font(.caption)
                                .foregroundColor(Color(.darkGray))

                            Text(review.text)
                                .font(.footnote)
                                .foregroundColor(.gray)
                                .fixedSize(horizontal: false, vertical: true)
                                .multilineTextAlignment(.leading)

                            if review.uid == sessionUid {
                                Button(role: .destructive) {
                                    onDelete(review.id)
                                } label: {
                                    Label("Delete Review", systemImage: "trash")
                                        .font(.footnote)
                                }
                            }
                        }
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color(.systemGray6))
                        .cornerRadius(10)
                        .padding(.horizontal)
                    }
                }
                .padding(.top, 10)
            }
        }
    }
}
