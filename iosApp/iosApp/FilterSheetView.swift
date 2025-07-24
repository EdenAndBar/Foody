import SwiftUI
import Sliders

struct FilterSheetView: View {
    @ObservedObject var filter: RestaurantFilter

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Filter")) {
                    Toggle("Open now only", isOn: $filter.onlyOpen)

                    VStack(alignment: .leading) {
                        Text("Rating range: \(filter.ratingRange.lowerBound, specifier: "%.1f") - \(filter.ratingRange.upperBound, specifier: "%.1f")")
                            .padding(.bottom, 4)

                        RangeSlider(range: $filter.ratingRange, in: 0...5, step: 0.5)
                            .frame(height: 40)
                    }
                    .padding(.vertical)
                }
            }
            .navigationBarItems(trailing:
                Button("Clear") {
                    filter.onlyOpen = false
                    filter.ratingRange = 0...5
                }
            )
            .navigationTitle("Filter")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
