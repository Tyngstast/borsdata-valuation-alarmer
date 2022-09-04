import Combine
import shared

class LoginViewModel: BaseViewModel<LoginCallbackViewModel> {
    private let log = koin.loggerWithTag(tag: "LoginViewModel")

    @Published var loading = false
    @Published var signedIn = false
    @Published var error: ErrorCode?

    override func activate() {
        let viewModel = ViewModels.shared.getLoginViewModel()

        doPublish(viewModel.apiKeyState) { [weak self] (keyState: ApiKeyState) in
            self?.log.d(message: { "API Key State: \(keyState)" })
            switch keyState {
            case is ApiKeyState.Loading:
                self?.loading = true
                return
            case is ApiKeyState.Success:
                self?.signedIn = true
                return
            case let state as ApiKeyState.Error:
                self?.error = state.errorCode
            case is ApiKeyState.Empty:
                self?.error = nil
            default:
                self?.log.d(message: { "default case cannot be reached since state is a sealed class" })
            }
            // all cases except Loading should stop loading
            self?.loading = false
            // All cases except Success is not signedIn
            self?.signedIn = false
        }
        .store(in: &cancellables)

        super.viewModel = viewModel
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
