
import Combine
import shared

class AlarmListViewModel : BaseViewModel<AlarmListCallbackViewModel> {
    private let log = koin.loggerWithTag(tag: "AlarmListViewModel")
    
    @Published var loading = false
    @Published var alarms: [Alarm] = []
    
    override func activate() {
        let viewModel = ViewModels.shared.getAlarmListViewModel()
        
        doPublish(viewModel.alarmListState) { [weak self] listState in
            self?.log.d(message: { "List State: \(listState)"})
            switch listState {
            case is AlarmListState.Loading:
                self?.loading = true
                return
            case let successState as AlarmListState.Success:
                self?.alarms = successState.alarms
            default:
                self?.log.d(message: { "default case cannot be reached since state is a sealed class" })
            }
            // all cases except loading == true should stop loading
            self?.loading = false
            
        }.store(in: &cancellables)
        
        super.viewModel = viewModel
    }
}
