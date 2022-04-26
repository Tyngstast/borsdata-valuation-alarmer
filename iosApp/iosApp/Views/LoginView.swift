import SwiftUI
import shared

struct LoginView: View {
    @ObservedObject var viewModel: LoginViewModel
    
    var body: some View {
        LoginViewContent(
            loading: viewModel.loading,
            error: viewModel.error,
            onVerify: { viewModel.verifyKey($0) },
            onClearErrror: { viewModel.clearError() }
        )
    }
}

struct LoginViewContent: View {
    var loading: Bool
    var error: ErrorCode?
    var onVerify: (String) -> Void
    var onClearErrror: () -> Void
    
    // Keep track of previous value to detect paste input
    @State var prevValue = ""
    @State var apiKey = ""
    
    var loginDisabled: Bool {
        return apiKey.count < 20 || loading || error != nil
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            SecureInputField("API Key", loading: loading, text: $apiKey)
                .onChange(of: apiKey) { value in
                    onClearErrror()
                    if (value.count - 2 > prevValue.count) {
                        onVerify(apiKey)
                    }
                    prevValue = value
                }
            if (error != nil) {
                HStack {
                    Image(systemName: "exclamationmark.circle.fill")
                    Text(error!.resourceId)
                }
                .foregroundColor(.errorColor)
            }
            Button(action: {
                guard !apiKey.isEmpty else {
                    return
                }
                onVerify(apiKey)
            }, label: {
                Text("VERIFY")
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: 50)
                    .background(loginDisabled ? Color.gray : Color.primaryColor)
                    .foregroundColor(loginDisabled ? .paleWhite : .white)
                    .cornerRadius(5.0)
                    .padding(.top, 7.5)
            })
            .disabled(loginDisabled)
        }
        .navigationTitle("Requires Börsdata Pro")
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .padding()
    }
}

struct SecureInputField: View {
    @Binding private var text: String
    @State private var isSecured: Bool = true
    private var loading: Bool
    private var title: String
    
    init(_ title: String, loading: Bool, text: Binding<String>) {
        self.title = title
        self.loading = loading
        self._text = text
    }
    
    var body: some View {
        ZStack(alignment: .trailing) {
            Group {
                if isSecured {
                    SecureField(title, text: $text)
                } else {
                    TextField(title, text: $text)
                        .keyboardType(.alphabet)
                }
            }
            .padding()
            .disableAutocorrection(true)
            .autocapitalization(.none)
            .frame(height: 60)
            .overlay(
                RoundedRectangle(cornerRadius: 5.0)
                    .strokeBorder(
                        Color.gray,
                        style: StrokeStyle(lineWidth: 2.0)
                    )
            )
            .background(Color.backgroundColor)
            
            Button(action: {
                isSecured.toggle()
            }) {
                if loading {
                    ProgressView()
                        .accentColor(.gray)
                } else {
                    Image(systemName: self.isSecured ? "eye.slash.fill" : "eye.fill")
                        .accentColor(.gray)
                }
            }
            .padding(.trailing, 16)
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
            NavigationView {
                LoginViewContent(
                    loading: false,
                    error: nil,
                    onVerify: { _ in },
                    onClearErrror: { }
                ).previewDevice(PreviewDevice(rawValue: "iPhone 11"))
            }
    }
}
