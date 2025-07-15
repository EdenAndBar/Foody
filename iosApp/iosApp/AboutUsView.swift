//import SwiftUI
//
//struct AboutUsView: View {
//    @Binding var showSidebar: Bool
//    @Environment(\.dismiss) var dismiss
//
//    var body: some View {
//        ZStack {
//            Image("backgroundImage")
//                .resizable()
//                .aspectRatio(contentMode: .fill)
//                .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
//                .opacity(1.0)
//                .offset(x: -20)
//                .ignoresSafeArea()
//
//            ScrollView {
//                VStack(spacing: 24) {
//                    Spacer().frame(height: 250)
//
//                    Text("Hungry?\nNot sure what to eat or where to go?\nThat’s exactly why we’re here!")
//                        .font(.title3)
//                        .multilineTextAlignment(.center)
//                        .padding(.horizontal)
//
//                    Divider()
//
//                    Text("""
//Foody helps you discover restaurants around you, based on your location or a city you choose.
//Feeling adventurous?\n Check out our 🏆 Top 10 recommended spots!
//""")
//                        .multilineTextAlignment(.center)
//                        .padding(.horizontal)
//
//                    Divider()
//
//                    VStack(spacing: 8) {
//                        Text("Love a restaurant?\nAdd it to your favorites ❤️\nChanged your mind?\nRemove it with a tap 💔")
//                            .font(.headline)
//                            .multilineTextAlignment(.center)
//                    }
//                    .padding()
//                    .padding(.horizontal)
//                    Spacer()
//                }
//            }
//        }
//        .navigationBarBackButtonHidden(true)
//        .toolbar {
//            ToolbarItem(placement: .navigationBarLeading) {
//                Button(action: {
//                    showSidebar = true
//                    dismiss()
//                }) {
//                    Label("Back", systemImage: "chevron.backward")
//                }
//            }
//        }
//    }
//}

import SwiftUI

struct AboutUsView: View {
    @Binding var showSidebar: Bool
    @Environment(\.dismiss) var dismiss

    var body: some View {
        ZStack {
            Image("backgroundImage")
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
                .opacity(1.0)
                .offset(x: -20)
                .ignoresSafeArea()

            ScrollView {
                VStack(spacing: 24) {
                    Spacer().frame(height: 300)

                    VStack(spacing: 8) {
                        Text("Hungry?")
                        Text("Not sure what to eat or where to go?")
                        Text("That’s exactly why we’re here!")
                    }
                    .font(.title3)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                    Divider()

                    VStack(spacing: 8) {
                        Text("Foody helps you discover restaurants around you,")
                        Text("based on your location or a city you choose.")
                        Text("Feeling adventurous?")
                        Text("Check out our 🏆 Top 10 recommended spots!")
                    }
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)

                    Divider()

                    VStack(spacing: 8) {
                        Text("Love a restaurant?")
                            .font(.headline)
                        Text("Add it to your favorites ❤️")
                            .font(.headline)
                        Text("Changed your mind?")
                            .font(.headline)
                        Text("Remove it with a tap 💔")
                            .font(.headline)
                    }
                    .multilineTextAlignment(.center)
                    .padding()
                    .padding(.horizontal)

                    Spacer()
                }
            }
        }
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    showSidebar = true
                    dismiss()
                }) {
                    Label("Back", systemImage: "chevron.backward")
                }
            }
        }
    }
}
