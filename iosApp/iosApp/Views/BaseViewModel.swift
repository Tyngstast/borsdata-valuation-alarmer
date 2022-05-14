import Combine
import shared

class BaseViewModel<T : CallbackViewModel> : ObservableObject {
    var viewModel: T?
    var cancellables = [AnyCancellable]()
    
    func activate() {
    }
    
    func deactivate() {
        cancellables.forEach { $0.cancel() }
        cancellables.removeAll()

        viewModel?.clear()
        viewModel = nil
    }
}
