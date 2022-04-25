
import Combine
import shared


class LoginViewModel : ObservableObject {
    private let log = koin.loggerWithTag(tag: "LoginViewModel")
    private var viewModel: LoginCallbackViewModel?
    
    @Published var loading = false
    @Published var signedIn = false
    @Published var error: ErrorCode?

    private var cancellables = [AnyCancellable]()
    
    func activate() {
        let viewModel = ViewModels.shared.getLoginViewModel()

        doPublish(viewModel.apiKeyState) { [weak self] keyState in
            self?.log.d(message: { "API Key State: \(keyState)"})
            switch keyState {
            case is ApiKeyState.Loading:
                self?.loading = true
            case is ApiKeyState.Success:
                self?.signedIn = true
            case let state as ApiKeyState.Error:
                self?.error = state.errorCode
            case is ApiKeyState.Empty:
                self?.error = nil
            default:
                self?.log.d(message: { "default case cannot be reached since state is a sealed class" })
            }
        }.store(in: &cancellables)

        self.viewModel = viewModel
    }

    func deactivate() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()

        viewModel?.clear()
        viewModel = nil
    }
    
    
    func clearKey() {
       viewModel?.clearKey()
    }

    func clearError() {
        viewModel?.clearError()
    }

    func verifyKey(_ key: String) {
        viewModel?.verifyKey(key: key)
    }
}
