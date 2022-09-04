import Combine
import shared

class AlarmListViewModel: BaseViewModel<AlarmListCallbackViewModel> {
    private let log = koin.loggerWithTag(tag: "AlarmListViewModel")

    @Published var loading = false
    @Published var alarms: [Alarm] = []

    override func activate() {
        let viewModel = ViewModels.shared.getAlarmListViewModel()

        doPublish(viewModel.alarmListState) { [weak self] (listState: AlarmListState) in
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

    func deleteAlarm(id: Int64) {
        super.viewModel?.deleteAlarm(id: id)
    }

    func updateDisabled(id: Int64, disable: Bool) {
        super.viewModel?.updateDisableAlarm(id: id, disable: disable)
    }
}
