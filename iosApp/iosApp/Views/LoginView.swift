import shared
import SwiftUI

struct LoginView: View {
    @StateObject var viewModel: LoginViewModel

    var body: some View {
        LoginViewContent(
            loading: viewModel.loading,
            error: viewModel.error,
            onVerify: { viewModel.verifyKey($0) },
            onClearError: { viewModel.clearError() }
        )
    }
}

struct LoginViewContent: View {
    var loading: Bool
    var error: ErrorCode?
    var onVerify: (String) -> Void
    var onClearError: () -> Void

    // Keep track of previous value to detect paste input
    @State var prevValue = ""
    @State var apiKey = ""

    var loginDisabled: Bool {
        apiKey.count < 20 || loading || error != nil
    }

    var body: some View {
        VStack(alignment: .leading) {
            SecureInputField(
                NSLocalizedString("login_text_input_label", comment: "API Key input hint"),
                loading: loading,
                password: $apiKey
            )
            .onChange(of: apiKey) { value in
                onClearError()
                if value.count - 5 > prevValue.count {
                    onVerify(apiKey)
                }
                prevValue = value
            }
            if error != nil {
                HStack {
                    Image(systemName: "exclamationmark.circle.fill")
                    Text(NSLocalizedString(error!.resourceId, comment: "Translation of error message"))
                }
                .foregroundColor(.errorColor)
            }
            Button(action: {
                guard !apiKey.isEmpty else {
                    return
                }
                onVerify(apiKey)
            }, label: {
                Text(NSLocalizedString("login_text_submit_button", comment: "Verify login button"))
                    .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: 50)
                    .background(loginDisabled ? Color.gray : Color.primaryColor)
                    .foregroundColor(loginDisabled ? .paleWhite : .white)
                    .cornerRadius(5.0)
                    .padding(.top, 7.5)
            })
            .disabled(loginDisabled)
        }
        .navigationTitle(NSLocalizedString("login_text_title", comment: "List alarms title"))
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .padding()
    }
}

struct SecureInputField: View {
    @FocusState private var focused: FocusedField?
    @Binding private var password: String
    @State private var showPassword: Bool = false
    private var loading: Bool
    private var title: String

    init(_ title: String, loading: Bool, password: Binding<String>) {
        self.title = title
        self.loading = loading
        _password = password
    }

    var body: some View {
        ZStack(alignment: .trailing) {
            Group {
                TextField(title, text: $password)
                    .focused($focused, equals: .unSecure)
                    // This is needed to remove suggestion bar, otherwise swapping between
                    // fields will change keyboard height and be distracting to user.
                    .keyboardType(.alphabet)
                    .opacity(showPassword ? 1 : 0)
                // SecureField still gets cleared on input after toggling showPassword
                SecureField(title, text: $password)
                    .focused($focused, equals: .secure)
                    .opacity(showPassword ? 0 : 1)
            }
            .padding()
            .onTapGesture {
                focused = focused == nil ? .secure : focused
            }
            .autocapitalization(.none)
            .disableAutocorrection(true)
            .frame(height: 60)
            .overlay(
                RoundedRectangle(cornerRadius: 5.0)
                    .strokeBorder(
                        Color.gray,
                        style: StrokeStyle(lineWidth: 2.0)
                    )
            )

            Button(action: {
                showPassword.toggle()
                focused = focused == .secure ? .unSecure : .secure
            }, label: {
                if loading {
                    ProgressView()
                        .accentColor(.gray)
                } else {
                    Image(systemName: self.showPassword ? "eye.slash.fill" : "eye.fill")
                        .accentColor(.gray)
                }
            })
            .padding(.trailing, 16)
        }
    }

    enum FocusedField {
        case secure, unSecure
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            LoginViewContent(
                loading: false,
                error: nil,
                onVerify: { _ in },
                onClearError: {}
            ).previewDevice(PreviewDevice(rawValue: "iPhone 11"))
        }
    }
}
