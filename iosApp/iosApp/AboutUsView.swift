import SwiftUI

struct AboutUsView: View {
    @State private var fadeIn = false
    @State private var scaleUp = false
    @Binding var showSidebar: Bool
    @Environment(\.dismiss) var dismiss

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Title
                Text("🍽️ Welcome to Foody")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
                    .padding(.top, 40)
                    .opacity(fadeIn ? 1 : 0)
                    .animation(.easeIn(duration: 1), value: fadeIn)

                // Description
                Text("""
Foody is your smart restaurant companion 🍔🍣🍕.

We help you discover the best places to eat around you – with live status (Open/Closed), authentic reviews, photos, ratings, opening hours, and real-time info – all in one app.

No more guessing. Just great food.
""")
                    .font(.body)
                    .multilineTextAlignment(.leading)
                    .padding(.horizontal)
                    .opacity(fadeIn ? 1 : 0)
                    .animation(.easeIn(duration: 1.4).delay(0.3), value: fadeIn)

                // Feature Card
                VStack(spacing: 16) {
                    Image(systemName: "fork.knife.circle.fill")
                        .resizable()
                        .frame(width: 80, height: 80)
                        .foregroundColor(.orange)

                    Text("Everything you need to know about restaurants – in one place")
                        .font(.headline)
                        .multilineTextAlignment(.center)

                    Text("Discover. Taste. Save your favorites. ❤️")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(20)
                .shadow(radius: 6)
                .scaleEffect(scaleUp ? 1 : 0.8)
                .animation(.spring(response: 0.6, dampingFraction: 0.5).delay(0.6), value: scaleUp)

                Spacer()
            }
            .padding(.bottom, 40)
        }
        .background(LinearGradient(
            gradient: Gradient(colors: [Color.white, Color(.systemGray6)]),
            startPoint: .top,
            endPoint: .bottom
        ))
        .onAppear {
            fadeIn = true
            scaleUp = true
        }
        .navigationTitle("About Us")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true) 
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    showSidebar = true
                    dismiss()
                }) {
                    HStack {
                        Image(systemName: "chevron.backward")
                        Text("Back")
                    }
                }
            }
        }
    }
}
