import SwiftUI

struct SidebarContainerView<Content: View>: View {
    @EnvironmentObject var session: UserSession
    @Binding var path: NavigationPath
    @Binding var isLoggedIn: Bool
    @Binding var showSidebar: Bool

    let content: () -> Content

    var body: some View {
        ZStack(alignment: .leading) {
            content()
                .environment(\.showSidebarBinding, $showSidebar)

            if showSidebar {
                Color.black.opacity(0.3)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation {
                            showSidebar = false
                        }
                    }

                VStack(alignment: .leading, spacing: 20) {
                    Spacer().frame(height: 80)

                    Button {
                        showSidebar = false
                        path.append("profile")
                    } label: {
                        Label("Profile", systemImage: "person")
                            .font(.headline)
                            .foregroundColor(.primary)
                    }

                    Button {
                        showSidebar = false
                        path.append("about")
                    } label: {
                        Label("About Us", systemImage: "info.circle")
                            .font(.headline)
                            .foregroundColor(.primary)
                    }

                    Button {
                        withAnimation {
                            showSidebar = false
                        }
                        isLoggedIn = false
                    } label: {
                        Label("Logout", systemImage: "arrow.backward.circle")
                            .font(.headline)
                            .foregroundColor(.red)
                    }

                    Spacer()
                }
                .frame(width: 220)
                .padding()
                .background(Color(UIColor.systemGray6))
                .transition(.move(edge: .leading))
                .animation(.easeInOut, value: showSidebar)
                .zIndex(1)
            }
        }
    }
}
