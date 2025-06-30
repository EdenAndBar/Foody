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
                        .animation(.easeInOut(duration: 0.3), value: photoUrl)
                } placeholder: {
                    Rectangle()
                        .fill(Color(.systemGray5))
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

            VStack(alignment: .leading, spacing: 4) {
                Text(name)
                    .font(.title3)
                    .fontWeight(.semibold)

                Text(address)
                    .font(.caption)
                    .foregroundColor(.gray)
                
                if let category = category?.lowercased(),
                   allowedCategories.contains(category) {
                    Text(category.uppercased())
                        .font(.caption)
                        .foregroundColor(categoryColors[category, default: .blue])
                }

                HStack(spacing: 6) {
                    Image(systemName: "star.fill")
                        .foregroundColor(.yellow)
                    Text(String(format: "%.1f", rating))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
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

let allowedCategories: Set<String> = [
    "pizza",
    "sushi",
    "burger",
    "cafe",
    "dessert",
    "asian",
    "italian",
    "indian",
    "chinese",
    "middle eastern",
    "home food",
    "mexican",
    "thai",
    "japanese",
    "korean",
    "lebanese",
    "bbq",
    "steakhouse",
    "vegan",
    "vegetarian",
    "seafood",
    "falafel",
    "shawarma",
    "grill",
    "mediterranean",
    "bistro",
    "brunch",
    "bakery",
    "ice cream",
    "donuts",
    "ramen",
    "tapas",
    "noodles"
]

let categoryColors: [String: Color] = [
    "pizza": .red,
    "sushi": .purple,
    "burger": .orange,
    "cafe": .brown,
    "dessert": .pink,
    "asian": .teal,
    "italian": .green,
    "indian": .yellow,
    "chinese": .mint,
    "falafel": .green,
    "shawarma": .red,
    "vegan": .green,
    "steakhouse": .gray,
    "bistro": .indigo,
    "bakery": .pink,
    "ice cream": .cyan,
    "ramen": .blue,
    "noodles": .orange
]


