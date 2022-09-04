import Combine
import shared

class EditAlarmViewModel: BaseViewModel<EditAlarmCallbackViewModel> {
    private let log = koin.loggerWithTag(tag: "EditAlarmViewModel")

    override func activate() {
        let viewModel = ViewModels.shared.getEditAlarmViewModel()
        super.viewModel = viewModel
    }

    func editAlarm(id: Int64, kpiValue: Double) {
        super.viewModel?.editAlarm(id: id, kpiValue: kpiValue)
    }
}
