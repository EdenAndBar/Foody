import SwiftUI
import Shared

struct BottomSheetView: View {
    let restaurant: Restaurant
    @Binding var favorites: [Restaurant]
    @Environment(\.dismiss) var dismiss
    
    var isFavorite: Bool {
        favorites.contains(restaurant)
    }
    
    var body: some View {
        VStack(spacing: 20) {
            Text(restaurant.name)
                .font(.headline)

            AsyncImage(url: URL(string: restaurant.url)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                ProgressView()
            }
            .frame(height: 150)
            .clipShape(RoundedRectangle(cornerRadius: 12))

            Button {
                if isFavorite {
                    favorites.removeAll { $0 == restaurant }
                } else {
                    favorites.append(restaurant)
                }
                dismiss()
            } label: {
                Label(
                    isFavorite ? "Remove from Favorites 💔" : "Add to Favorites ❤️",
                    systemImage: isFavorite ? "heart.slash" : "heart.fill"
                )
                .padding()
                .frame(maxWidth: .infinity)
                .background(isFavorite ? Color.gray : Color.red)
                .foregroundColor(.white)
                .cornerRadius(12)
            }


            Spacer()
        }
        .padding()
        .presentationDetents([.medium])
    }
}


