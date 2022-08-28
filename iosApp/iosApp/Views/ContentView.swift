import shared
import SwiftUI

struct ContentView: View {
    @StateObject var viewModel = LoginViewModel()

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.signedIn {
                    ListView(onResetKey: viewModel.clearKey)
                } else {
                    LoginView(viewModel: viewModel)
                }
            }
        }
        .onAppear(perform: viewModel.activate)
        .onDisappear(perform: viewModel.deactivate)
        .accentColor(.white)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
