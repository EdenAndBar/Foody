//import SwiftUI
//
//struct RestaurantCard: View {
//    let name: String
//    let photoUrl: String
//    let address: String
//    let rating: Float
//    let isOpenNow: Bool?
//
//    var body: some View {
//        VStack(alignment: .leading, spacing: 8) {
//            AsyncImage(url: URL(string: photoUrl)) { image in
//                image
//                    .resizable()
//                    .aspectRatio(contentMode: .fill)
//            } placeholder: {
//                ProgressView()
//            }
//            .frame(height: 200)
//            .clipped()
//            .cornerRadius(12)
//
//            Text(name)
//                .font(.headline)
//
//            Text(address)
//                .font(.subheadline)
//                .foregroundColor(.gray)
//
//            HStack(spacing: 8) {
//                Image(systemName: "star.fill")
//                    .foregroundColor(.yellow)
//                Text(String(format: "%.1f", rating))
//                    .font(.subheadline)
//
//                if let isOpen = isOpenNow {
//                    Circle()
//                        .fill(isOpen ? Color.green : Color.red)
//                        .frame(width: 8, height: 8)
//                    Text(isOpen ? "Open Now" : "Closed")
//                        .font(.subheadline)
//                        .foregroundColor(isOpen ? .green : .red)
//                }
//
//                Spacer()
//            }
//        }
//        .padding()
//        .background(Color(.systemGray6))
//        .cornerRadius(12)
//        .shadow(radius: 2)
//    }
//}
//

import SwiftUI

struct RestaurantCard: View {
    let name: String
    let photoUrl: String
    let address: String
    let rating: Float
    let isOpenNow: Bool?

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

                // Open/Closed Badge
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
        .background(Color.white)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 4)
        .padding(.horizontal)
    }
}

