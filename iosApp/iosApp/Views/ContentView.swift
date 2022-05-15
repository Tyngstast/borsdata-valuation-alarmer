import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject var viewModel = LoginViewModel()
    
	var body: some View {
        NavigationView {
            VStack {
                if viewModel.signedIn {
                    ListView()
                        .onAppear(perform: {
                            viewModel.deactivate()
                        })
                } else {
                    LoginView(viewModel: viewModel)
                }
            }
        }.onAppear(perform: {
            viewModel.activate()
        }).onDisappear(perform: {
            viewModel.deactivate()
        })
        .accentColor(.white)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
