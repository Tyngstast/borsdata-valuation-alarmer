import SwiftUI

struct RootNavigation<Content: View> : View {
    var content: Content
    
    init(@ViewBuilder content: () -> (Content)) {
        self.content = content()
    }
    
    var body:some View {
        EmptyView()
            .onAppear {
                UIApplication.shared.setHostingController(rootView: AnyView(content))
            }
    }
}
