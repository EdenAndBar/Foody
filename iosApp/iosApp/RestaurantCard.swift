import SwiftUI

struct RestaurantCard: View {
    let name: String
    let photoUrl: String
    let address: String
    let rating: Float
    let isOpenNow: Bool?

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            AsyncImage(url: URL(string: photoUrl)) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                ProgressView()
            }
            .frame(height: 200)
            .clipped()
            .cornerRadius(12)

            Text(name)
                .font(.headline)

            Text(address)
                .font(.subheadline)
                .foregroundColor(.gray)

            HStack(spacing: 8) {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                Text(String(format: "%.1f", rating))
                    .font(.subheadline)

                if let isOpen = isOpenNow {
                    Circle()
                        .fill(isOpen ? Color.green : Color.red)
                        .frame(width: 8, height: 8)
                    Text(isOpen ? "Open Now" : "Closed")
                        .font(.subheadline)
                        .foregroundColor(isOpen ? .green : .red)
                }

                Spacer()
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .shadow(radius: 2)
    }
}

