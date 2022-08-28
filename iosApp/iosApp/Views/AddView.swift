import shared
import SwiftUI

struct AddView: View {
    private let log = koin.loggerWithTag(tag: "AddView")

    @StateObject var viewModel = AddViewModel()

    func onInsNameChange(value: String) {
        log.d(message: { "onInsNameChange: \(value)" })
        viewModel.getInstruments(value: value)
    }

    func onKpiNameChange(value: String) {
        log.d(message: { "onKpiNameChange: \(value)" })
        viewModel.getKpis(value: value)
    }

    var body: some View {
        AddViewContent(
            instruments: viewModel.instruments,
            kpis: viewModel.kpis,
            onInsNameChange: onInsNameChange,
            onKpiNameChange: onKpiNameChange,
            onAdd: viewModel.addAlarm
        )
        .onAppear(perform: viewModel.activate)
        .onDisappear(perform: viewModel.deactivate)
    }
}

struct AddViewContent: View {
    private let log = koin.loggerWithTag(tag: "AddViewContent")
    var instruments: [Item]
    var kpis: [Item]
    var onInsNameChange: (String) -> Void
    var onKpiNameChange: (String) -> Void
    var onAdd: (Double) -> Void

    @State var insName = ""
    @State var kpiName = ""
    @State var kpiValue = ""

    // TODO: Fix alignment of input + suggestions
    // TODO: click on suggestion autocompletes
    // TODO: shift focus hides suggestions
    // TODO: actually add
    // TODO: clean up styling implementation
    // TODO: renaming and move to subpackages of Views
    func addAlarm() {
        log.d(message: { "add alarm: \(kpiValue)" })
        guard let value = Double(kpiValue.replacingOccurrences(of: ",", with: ".")) else {
            log.e(message: { "value is not double: \(kpiValue)" })
            return
        }
        log.d(message: { "value parsed: \(value)" })
//        onAdd(value)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                TextField(NSLocalizedString("company_label", comment: "Company name"), text: $insName)
                    .onChange(of: insName, perform: onInsNameChange)
                ForEach(instruments, id: \.id) { instrument in
                    Text(instrument.name)
                }
            }
            .frame(minHeight: 60)
            .padding(.horizontal, 12)
            Divider()
                .frame(height: 2)
                .background(Color(.systemGray5))
            VStack(alignment: .leading, spacing: 0) {
                TextField(NSLocalizedString("kpi_label", comment: "KPI name"), text: $kpiName)
                    .onChange(of: kpiName, perform: onKpiNameChange)
                ForEach(kpis, id: \.id) { kpi in
                    Text(kpi.name)
                }
            }
            .frame(minHeight: 60)
            .padding(.horizontal, 12)
            Divider()
                .frame(height: 2)
                .background(Color(.systemGray5))
            VStack {
                TextField(NSLocalizedString("kpi_below_threshold_label", comment: "Trigger when KPI below value"), text: $kpiValue)
                    .onChange(of: kpiValue, perform: { _ in })
                    .keyboardType(.numbersAndPunctuation)
            }
            .frame(minHeight: 60)
            .padding(.horizontal, 12)
            Divider()
                .frame(height: 2)
                .background(Color(.systemGray5))
        }
        .navigationTitle(NSLocalizedString("add_text_title", comment: "Add alarm title"))
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: addAlarm) {
                    Image(systemName: "checkmark")
                        .foregroundColor(Color.secondaryColor)
                }
            }
        }
        .frame(minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct AddView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
//            AddViewContent(
//                instruments: [],
//                kpis: [],
//                onAdd: { _ in }
//            )
        }
        .previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
