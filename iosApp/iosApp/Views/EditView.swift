import AlertToast
import shared
import SwiftUI

struct EditView: View {
    private let log = koin.loggerWithTag(tag: "EditView")

    @StateObject var viewModel = EditAlarmViewModel()

    var alarm: Alarm

    var body: some View {
        EditViewContent(
            alarm: alarm,
            onEdit: { v in
                viewModel.editAlarm(id: alarm.id, kpiValue: v)
            }
        )
        .onAppear(perform: viewModel.activate)
        .onDisappear(perform: viewModel.deactivate)
    }
}

struct EditViewContent: View {
    private let log = koin.loggerWithTag(tag: "EditViewContent")

    @Environment(\.dismiss) var dismissView

    @FocusState var focused: Bool
    @State var showToast = false
    @State var value: String

    var alarm: Alarm
    var onEdit: (Double) -> Void

    init(alarm: Alarm, onEdit: @escaping (Double) -> Void) {
        self.alarm = alarm
        self.onEdit = onEdit
        _value = State(initialValue: String(format: "%.1f", alarm.kpiValue))
    }

    func onEdit(kpiValue: String) {
        guard let kpiDoubleValue = Double(kpiValue.replacingOccurrences(of: ",", with: ".")) else {
            showToast = true
            return
        }
        onEdit(kpiDoubleValue)
        dismissView()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(alarm.insName)
                .foregroundColor(Color(.systemGray))
                .frame(minHeight: 50)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
            InputFieldDivider(isFocused: false)
            Text(alarm.kpiName)
                .foregroundColor(Color(.systemGray))
                .frame(minHeight: 50)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
            InputFieldDivider(isFocused: false)
            InputField(
                label: NSLocalizedString("kpi_below_threshold_label", comment: "Trigger when KPI below value"),
                value: $value,
                onInputChange: { _ in },
                setFocus: {
                    focused = true
                },
                isFocused: focused
            )
            .focused($focused)
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.focused = true
            }
        }
        .navigationTitle(NSLocalizedString("edit_text_title", comment: "Edit alarm title"))
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: {
                    onEdit(kpiValue: value)
                }, label: {
                    Image(systemName: "checkmark")
                        .foregroundColor(Color.secondaryColor)
                })
            }
        }
        .toast(isPresenting: $showToast, duration: 2.0, alert: {
            AlertToast(type: .regular, title: NSLocalizedString("edit_toast_invalid_kpi_value", comment: "Invalid input toast"))
        }, completion: {
            showToast = false
        })
        .frame(minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct EditView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
//            EditViewContent()
        }.previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
