import SwiftUI

struct LoginView: View {
    @ObservedObject var viewModel: LoginViewModel
    
    var body: some View {
        LoginViewContent(
            onVerify: { viewModel.verifyKey($0) }
        )
    }
}

struct LoginViewContent: View {
    var onVerify: (String) -> Void
    
    @State var apiKey = ""
    
    var body: some View {
        VStack {
            SecureField("API Key", text: $apiKey)
                .disableAutocorrection(true)
                .autocapitalization(.none)
                .padding()
            Button(action: {
                // validate input
                guard !apiKey.isEmpty else {
                    return
                }
                onVerify(apiKey)
            }, label: {
                Text("VERIFY")
                    .background(Color.blue)
            })
            
        }.navigationTitle("Requires Börsdata Pro")
        .padding()
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginViewContent(
            onVerify: { _ in }
        ).previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
