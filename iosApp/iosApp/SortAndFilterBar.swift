import SwiftUI

struct SortAndFilterBar: View {
    @ObservedObject var filter: RestaurantFilter
    @Binding var showFilterSheet: Bool

    var body: some View {
        HStack(spacing: 12) {
            Spacer()

            Menu {
                Section("Sort by") {
                    Button("Name") { filter.sortField = .name }
                    Button("Rating") { filter.sortField = .rating }
                }
                Section("Direction") {
                    Button {
                        filter.sortDirection = filter.sortDirection == .ascending ? .descending : .ascending
                    } label: {
                        Label(
                            filter.sortDirection == .ascending ? "Ascending ↑" : "Descending ↓",
                            systemImage: filter.sortDirection == .ascending ? "arrow.up" : "arrow.down"
                        )
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

            Spacer()
        }
        .padding(.horizontal)
    }
}
