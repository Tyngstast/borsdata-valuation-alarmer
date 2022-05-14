import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject var viewModel = LoginViewModel()
    
	var body: some View {
//        let _ = viewModel.clearKey()
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
            }.padding(.top, 10)
        }.onAppear(perform: {
            viewModel.activate()
        }).onDisappear(perform: {
            viewModel.deactivate()
        })
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
