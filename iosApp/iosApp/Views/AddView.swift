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

    @FocusState private var focused: FocusField?
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
                },
                setFocus: {
                    focused = .ins
                },
                focusNext: {
                    focused = .kpi
                },
                isFocused: focused == .ins
            )
            .focused($focused, equals: .ins)
            SuggestionInputField(
                items: kpis,
                label: NSLocalizedString("kpi_label", comment: "KPI name"),
                text: $kpiName,
                onInputChange: { v in
                    kpiName = v
                    onKpiNameChange(v)
                },
                setFocus: {
                    focused = .kpi
                },
                focusNext: {
                    focused = .kpiValue
                },
                isFocused: focused == .kpi
            )
            .focused($focused, equals: .kpi)
            InputField(
                label: NSLocalizedString("kpi_below_threshold_label", comment: "Trigger when KPI below value"),
                text: $kpiValue,
                onInputChange: { v in
                    kpiValue = v
                },
                setFocus: {
                    focused = .kpiValue
                },
                isFocused: focused == .kpiValue
            )
            .keyboardType(.numbersAndPunctuation)
            .focused($focused, equals: .kpiValue)
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.focused = .ins
            }
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
    var setFocus: () -> Void
    var focusNext: () -> Void
    @State var showSuggestions = true
    var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            InputField(
                label: label,
                text: $text,
                onInputChange: { v in
                    onInputChange(v)
                    showSuggestions = true
                },
                setFocus: setFocus,
                isFocused: isFocused
            )
            if !text.isEmpty && showSuggestions && isFocused {
                ForEach(items, id: \.id) { item in
                    HStack {
                        Text(item.name)
                            .padding(.vertical, 6)
                        Spacer()
                        if item is KpiItem && (item as! KpiItem).fluent {
                            Image(systemName: "bolt.fill")
                        }
                    }
                    .onTapGesture {
                        onInputChange(item.name)
                        showSuggestions = false
                        focusNext()
                    }
                }
                .padding(.horizontal, 12)
            }
        }
        .animation(.interactiveSpring(), value: items)
    }
}

struct InputField: View {
    private let log = koin.loggerWithTag(tag: "InputField")
    var label: String
    @Binding var text: String
    var onInputChange: (String) -> Void
    var setFocus: () -> Void
    var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            TextField(label, text: $text)
                .onChange(of: text, perform: onInputChange)
                .frame(minHeight: 50)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
            Divider()
                .frame(height: isFocused ? 2.5 : 2)
                .background(Color(isFocused ? .primaryColor : .systemGray5))
        }
        .onTapGesture(perform: setFocus)
    }
}

enum FocusField {
    case ins, kpi, kpiValue
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
