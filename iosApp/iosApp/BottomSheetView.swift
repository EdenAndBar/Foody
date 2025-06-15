//import SwiftUI
//import Shared
//
//struct BottomSheetView: View {
//    let restaurant: Restaurant
//    @Binding var favorites: [Restaurant]
//    @Environment(\.dismiss) var dismiss
//    
//    var isFavorite: Bool {
//        favorites.contains(restaurant)
//    }
//    
//    var body: some View {
//        VStack(spacing: 20) {
//            Text(restaurant.name)
//                .font(.headline)
//
//            AsyncImage(url: URL(string: restaurant.url)) { image in
//                image.resizable().scaledToFill()
//            } placeholder: {
//                ProgressView()
//            }
//            .frame(height: 150)
//            .clipShape(RoundedRectangle(cornerRadius: 12))
//
//            Button {
//                if isFavorite {
//                    favorites.removeAll { $0 == restaurant }
//                } else {
//                    favorites.append(restaurant)
//                }
//                dismiss()
//            } label: {
//                Label(
//                    isFavorite ? "Remove from Favorites 💔" : "Add to Favorites ❤️",
//                    systemImage: isFavorite ? "heart.slash" : "heart.fill"
//                )
//                .padding()
//                .frame(maxWidth: .infinity)
//                .background(isFavorite ? Color.gray : Color.red)
//                .foregroundColor(.white)
//                .cornerRadius(12)
//            }
//
//
//            Spacer()
//        }
//        .padding()
//        .presentationDetents([.medium])
//    }
//}
//
//

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
                .font(.title2)
                .bold()

            AsyncImage(url: URL(string: restaurant.photoUrl)) { image in
                image.resizable().scaledToFill()
            } placeholder: {
                ProgressView()
            }
            .frame(height: 150)
            .clipShape(RoundedRectangle(cornerRadius: 12))

            // ⭐️ דירוג
            HStack {
                Image(systemName: "star.fill")
                    .foregroundColor(.yellow)
                Text(String(format: "%.1f", restaurant.rating))
                    .font(.subheadline)
            }

            // 📝 תיאור (אם קיים)
            if !restaurant.address.isEmpty {
                Text(restaurant.address)
                    .font(.body)
                    .foregroundColor(.gray)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }

            Button {
                if isFavorite {
                    favorites.removeAll { $0 == restaurant }
                } else {
                    favorites.append(restaurant)
                }
                dismiss()
            } label: {
                Label(
                    isFavorite ? "Remove from Favorites" : "Add to Favorites",
                    systemImage: isFavorite ? "heart.slash" : "heart.fill"
                )
                .padding()
                .frame(maxWidth: .infinity)
                .background(isFavorite ? Color.gray : Color.pink)
                .foregroundColor(.white)
                .cornerRadius(12)
            }

            Spacer()
        }
        .padding()
        .presentationDetents([.medium])
    }
}

