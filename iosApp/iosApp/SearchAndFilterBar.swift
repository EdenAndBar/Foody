import SwiftUI

struct SearchAndFilterBar: View {
    @Binding var searchText: String
    @ObservedObject var filter: RestaurantFilter
    @Binding var showFilterSheet: Bool
    var isLocationMode: Bool = false // ✅ חדש

    var body: some View {
        HStack(spacing: 8) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                TextField(
                    isLocationMode ? "Search city..." : "Search restaurants...",
                    text: $searchText
                )
                .autocapitalization(.none)
                .disableAutocorrection(true)
            }
            .padding(10)
            .background(Color(UIColor.systemGray6))
            .cornerRadius(20)

            Menu {
                Section("Sort by") {
                    Button("Name") { filter.sortField = .name }
                    Button("Rating") { filter.sortField = .rating }
                }
                Section("Direction") {
                    Button {
                        filter.sortDirection = filter.sortDirection == .ascending ? .descending : .ascending
                    } label: {
                        Label(filter.sortDirection == .ascending ? "Ascending ↑" : "Descending ↓",
                              systemImage: filter.sortDirection == .ascending ? "arrow.up" : "arrow.down")
                    }
                }
            } label: {
                Image(systemName: "arrow.up.arrow.down")
                    .padding(8)
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(8)
            }

            Button {
                showFilterSheet = true
            } label: {
                Image(systemName: "line.3.horizontal.decrease.circle")
                    .padding(8)
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(8)
            }
        }
        .padding(.horizontal)
    }
}
