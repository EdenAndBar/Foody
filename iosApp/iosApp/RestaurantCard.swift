import SwiftUI

struct RestaurantCard: View {
    let name: String
    let photoUrl: String
    let address: String
    let rating: Float

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            AsyncImage(url: URL(string: photoUrl)) { image in
                image
                    .resizable()
                    .scaledToFill()
                    .frame(height: 200)
                    .clipped()
            } placeholder: {
                ProgressView()
                    .frame(height: 200)
            }

            VStack(alignment: .leading, spacing: 6) {
                Text(name)
                    .font(.headline)

                Text(address)
                    .font(.subheadline)
                    .foregroundColor(.gray)
                    .lineLimit(2)

                HStack {
                    Image(systemName: "star.fill")
                        .foregroundColor(.yellow)
                    Text(String(format: "%.1f", rating))
                        .font(.subheadline)
                }
            }
            .padding()
        }
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 4)
    }
}
