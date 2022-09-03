import AlertToast
import shared
import SwiftUI

struct AddView: View {
    private let log = koin.loggerWithTag(tag: "AddView")

    @StateObject var viewModel = AddViewModel()

    func onInsNameChange(value: String) {
        viewModel.getInstruments(value: value)
    }

    func onKpiNameChange(value: String) {
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
    @Environment(\.dismiss) var dismissView

    var instruments: [Item]
    var kpis: [Item]
    var onInsNameChange: (String) -> Void
    var onKpiNameChange: (String) -> Void
    var onAdd: (Double) -> Void

    @FocusState private var isFocused: Bool
    @State var showToast = false
    @State var toastMsg = ""
    @State var insName = ""
    @State var kpiName = ""
    @State var kpiValue = ""

    func addAlarm() {
        guard !insName.isEmpty, !kpiName.isEmpty, !kpiValue.isEmpty else {
            toastMsg = NSLocalizedString("add_toast_empty_input", comment: "Empty input toast")
            showToast = true
            return
        }
        guard let value = Double(kpiValue.replacingOccurrences(of: ",", with: ".")) else {
            toastMsg = NSLocalizedString("add_toast_invalid_kpi_value", comment: "Invalid trigger value toast")
            showToast = true
            return
        }
        onAdd(value)
        dismissView()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            SuggestionInputField(
                items: instruments,
                label: NSLocalizedString("company_label", comment: "Company name"),
                text: $insName,
                onInputChange: { v in
                    insName = v
                    onInsNameChange(v)
                }
            )
            SuggestionInputField(
                items: kpis,
                label: NSLocalizedString("kpi_label", comment: "KPI name"),
                text: $kpiName,
                onInputChange: { v in
                    kpiName = v
                    onKpiNameChange(v)
                }
            )
            TextField(NSLocalizedString("kpi_below_threshold_label", comment: "Trigger when KPI below value"), text: $kpiValue)
                .onChange(of: kpiValue, perform: { _ in })
                .focused($isFocused)
                .onTapGesture {
                    isFocused = true
                }
                .keyboardType(.numbersAndPunctuation)
                .frame(minHeight: 50)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
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
        .toast(isPresenting: $showToast, duration: 2.0, alert: {
            AlertToast(type: .regular, title: toastMsg)
        }, completion: {
            showToast = false
        })
        .frame(minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct SuggestionInputField: View {
    var items: [Item]
    var label: String
    @Binding var text: String
    var onInputChange: (String) -> Void
    @State var showSuggestions = true
    @FocusState private var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            TextField(label, text: $text)
                .onChange(of: text, perform: onInputChange)
                .focused($isFocused)
                .onTapGesture {
                    isFocused = true
                }
                .frame(minHeight: 50)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
            Divider()
                .frame(height: 2)
                .background(Color(.systemGray5))
            if showSuggestions {
                ForEach(items, id: \.id) { item in
                    HStack {
                        Text(item.name)
                            .onTapGesture {
                                onInputChange(item.name)
                                showSuggestions = false
                            }
                            .padding(.vertical, 6)
                        Spacer()
                        if item is KpiItem && (item as! KpiItem).fluent {
                            Image(systemName: "bolt.fill")
                        }
                    }
                }
                .padding(.horizontal, 12)
            }
        }
        .animation(.interactiveSpring(), value: items)
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
