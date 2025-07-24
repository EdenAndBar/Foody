import SwiftUI

struct SearchAndFilterBar: View {
    @Binding var searchText: String
    @ObservedObject var filter: RestaurantFilter
    @Binding var showFilterSheet: Bool
    var isLocationMode: Bool = false

    @Environment(\.showSidebarBinding) private var showSidebar

    var body: some View {
        HStack(spacing: 12) {
            // ☰ כפתור סיידבר
            if let showSidebar = showSidebar {
                Button {
                    withAnimation {
                        showSidebar.wrappedValue.toggle()
                    }
                } label: {
                    Image(systemName: "line.3.horizontal")
                        .font(.title2)
                        .padding(8)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(10)
                }
            }

            // 🔍 שדה חיפוש
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
            .frame(maxWidth: .infinity)

            // ⇅ מיון
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

                // 🔴 Reset מתחת ל־Direction
                Button(role: .destructive) {
                    filter.sortField = .name
                    filter.sortDirection = .ascending
                } label: {
                    Text("Reset")
                }

            } label: {
                Image(systemName: "arrow.up.arrow.down")
                    .padding(8)
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(8)
            }

            // ⚙️ סינון
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
        .padding(.top, 8)
    }
}
