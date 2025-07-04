import SwiftUI
import Shared

struct RestaurantRow: View {
    let restaurant: Restaurant
    let isFavorite: Bool
    let onTap: () -> Void

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(restaurant.name)
                    .font(.headline)

                Text(restaurant.address)
                    .font(.subheadline)
                    .foregroundColor(.gray)

                Text("Rating: \(String(format: "%.1f", restaurant.rating))")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Spacer()

            Image(systemName: isFavorite ? "heart.fill" : "heart")
                .foregroundColor(.red)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
        .onTapGesture {
            onTap()
        }
    }
}
