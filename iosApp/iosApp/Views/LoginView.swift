import SwiftUI
import shared

struct LoginView: View {
    @ObservedObject var viewModel: LoginViewModel
    
    var body: some View {
        LoginViewContent(
            loading: viewModel.loading,
            error: viewModel.error,
            onVerify: { viewModel.verifyKey($0) }
        )
    }
}

struct LoginViewContent: View {
    var loading: Bool
    var error: ErrorCode?
    var onVerify: (String) -> Void
    
    @State var apiKey = ""
    
    var body: some View {
        VStack(alignment: .leading) {
            SecureInputField("API Key", text: $apiKey)
                .disableAutocorrection(true)
                .autocapitalization(.none)
                .padding()
                .frame(height: 60)
                .overlay(
                    RoundedRectangle(cornerRadius: 5.0)
                        .strokeBorder(
                            Color.gray,
                            style: StrokeStyle(lineWidth: 2.0)
                        )
                )
                .background(Color.backgroundColor)
            if (error != nil) {
                HStack {
                    Image(systemName: "exclamationmark.circle.fill")
                    Text("Error")
                }
                .foregroundColor(.errorColor)
            }
            Button(action: {
                // validate input
                guard !apiKey.isEmpty else {
                    return
                }
                onVerify(apiKey)
            }, label: {
                Text("VERIFY")
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: 50)
                    .background(Color.primaryColor)
                    .foregroundColor(.white)
                    .cornerRadius(5.0)
                    .padding(.top, 7.5)
            })
            
        }
        .navigationTitle("Requires Börsdata Pro")
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .padding()
    }
}

struct SecureInputField: View {
    @Binding private var text: String
    @State private var isSecured: Bool = true
    private var title: String
    
    init(_ title: String, text: Binding<String>) {
        self.title = title
        self._text = text
    }
    
    var body: some View {
        ZStack(alignment: .trailing) {
            Group {
                if isSecured {
                    SecureField(title, text: $text)
                } else {
                    TextField(title, text: $text)
                }
            }.padding(.trailing, 32)

            Button(action: {
                isSecured.toggle()
            }) {
                Image(systemName: self.isSecured ? "eye.slash" : "eye")
                    .accentColor(.gray)
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            LoginViewContent(
                loading: false,
                error: nil,
                onVerify: { _ in }
            ).previewDevice(PreviewDevice(rawValue: "iPhone 11"))
        }
    }
}
