import SwiftUI

struct RestaurantCard: View {
    let name: String
    let photoUrl: String
    let address: String
    let rating: Float
    let isOpenNow: Bool?
    let category: String?

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            ZStack(alignment: .topTrailing) {
                AsyncImage(url: URL(string: photoUrl)) { image in
                    image
                        .resizable()
                        .scaledToFill()
                        .transition(.opacity)
                        .frame(width: UIScreen.main.bounds.width - 32, height: 200)
                        .animation(.easeInOut(duration: 0.3), value: photoUrl)
                } placeholder: {
                    Rectangle()
                        .fill(Color(.systemGray5))
                        .frame(width: UIScreen.main.bounds.width - 32, height: 200)
                        .overlay(ProgressView())
                }
                .frame(height: 180)
                .clipped()
                .cornerRadius(16)
                
                if let open = isOpenNow {
                    Text(open ? "Open" : "Closed")
                        .font(.caption)
                        .bold()
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(open ? Color.green.opacity(0.8) : Color.red.opacity(0.8))
                        .foregroundColor(.white)
                        .clipShape(Capsule())
                        .padding(10)
                }
            }
            
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(name)
                        .font(.headline)
                        .fontWeight(.semibold)

                    Text("📍 \(address)")
                        .font(.caption)
                        .foregroundColor(.gray)

                    if let category = category?.trimmingCharacters(in: .whitespacesAndNewlines),
                       !category.isEmpty {
                        Text(category.capitalized)
                            .font(.caption)
                            .foregroundColor(categoryColors[category.lowercased(), default: .blue])
                    }
                }

                Spacer()

                VStack {
                    Spacer()
                    HStack(spacing: 4) {
                        Image(systemName: "star.fill")
                            .foregroundColor(.yellow)
                        Text(String(format: "%.1f", rating))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }
            }
            .padding(.horizontal)
            .padding(.bottom, 12)

        }
        .background(Color(.systemGray6))
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 4)
    }
}
