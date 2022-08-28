import Combine
import shared

class AlarmListViewModel: BaseViewModel<AlarmListCallbackViewModel> {
    private let log = koin.loggerWithTag(tag: "AlarmListViewModel")

    @Published var loading = false
    @Published var alarms: [Alarm] = []

    override func activate() {
        let viewModel = ViewModels.shared.getAlarmListViewModel()

        doPublish(viewModel.alarmListState) { [weak self] (listState: AlarmListState) in
            self?.log.d(message: { "List State: \(listState)" })
            switch listState {
            case is AlarmListState.Loading:
                self?.loading = true
                return
            case let successState as AlarmListState.Success:
//                self?.alarms = successState.alarms
                self?.alarms = [
                    Alarm(
                        id: 1,
                        insId: 1,
                        insName: "Evolution",
                        yahooId: "1",
                        kpiId: 1,
                        kpiName: "P/E",
                        kpiValue: 30.0,
                        operation: "lt",
                        disabled: true
                    ),
                    Alarm(
                        id: 2,
                        insId: 2,
                        insName: "Brdr. A&O Johansen",
                        yahooId: "2",
                        kpiId: 2,
                        kpiName: "EV/EBITDA",
                        kpiValue: 5.5,
                        operation: "lt",
                        disabled: false
                    )
                ]
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
