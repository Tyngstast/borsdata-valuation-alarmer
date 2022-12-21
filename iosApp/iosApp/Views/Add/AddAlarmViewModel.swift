import Combine
import shared

class AddViewModel: BaseViewModel<AddAlarmCallbackViewModel> {
    private let log = koin.loggerWithTag(tag: "AddViewModel")

    @Published var instruments: [InsItem] = []
    @Published var kpis: [KpiItem] = []

    override func activate() {
        let viewModel = ViewModels.shared.getAddAlarmViewModel()

        doPublish(viewModel.instruments) { [weak self] (instruments: NSArray) in
            self?.instruments = instruments as! [InsItem]
        }.store(in: &cancellables)

        doPublish(viewModel.kpis) { [weak self] (kpis: NSArray) in
            self?.kpis = kpis as! [KpiItem]
        }.store(in: &cancellables)

        super.viewModel = viewModel
    }

    func getInstruments(value: String) {
        super.viewModel?.getInstruments(value: value)
    }

    func getKpis(value: String) {
        super.viewModel?.getKpis(value: value)
    }

    func addAlarm(kpiValue: Double, isAbove: Bool) {
        super.viewModel?.addAlarm(kpiValue: kpiValue, isAbove: isAbove)
    }
}
