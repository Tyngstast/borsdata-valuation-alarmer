import FirebaseMessaging
import shared
import SwiftUI

let TOPIC = "triggerValuationAlarmWorker"

struct ListView: View {
    @StateObject var viewModel = AlarmListViewModel()
    var alarmWorkerModel = Models.shared.getValuationAlarmWorkerModel()
    var onResetKey: () -> Void

    func logout() {
        Messaging.messaging().unsubscribe(fromTopic: TOPIC)
        onResetKey()
    }

    var body: some View {
        ListViewContent(
            loading: viewModel.loading,
            alarms: viewModel.alarms,
            onDelete: viewModel.deleteAlarm,
            onUpdateDisabled: viewModel.updateDisabled,
            onResetKey: logout
        )
        .onAppear(perform: {
            UNUserNotificationCenter.current()
                .requestAuthorization(options: [.alert, .badge]) { _, _ in }
            Messaging.messaging().subscribe(toTopic: TOPIC)
            viewModel.activate()
        })
    }
}

struct ListViewContent: View {
    var loading: Bool
    var alarms: [Alarm]
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    var onResetKey: () -> Void

    var body: some View {
        VStack {
            if !alarms.isEmpty {
                AlarmListContent(
                    alarms: alarms,
                    onDelete: onDelete,
                    onUpdateDisabled: onUpdateDisabled
                )
            } else if !loading {
                WelcomeInfoContent()
            }
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    NavigationLink(destination: NavigationLazyView(AddView())) {
                        Text("+")
                            .font(.system(.largeTitle))
                            .frame(width: 66, height: 62)
                            .foregroundColor(.foregroundItemColor)
                            .padding(.bottom, 4)
                    }
                    .background(Color.secondaryColor)
                    .cornerRadius(38.5)
                    .padding()
                    .shadow(
                        color: Color.black.opacity(0.3),
                        radius: 3,
                        x: 3,
                        y: 3
                    )
                }
            }
        }
        .navigationTitle(NSLocalizedString("list_text_title", comment: "List alarms title"))
        .toolbar {
            Menu {
                Button(action: onResetKey) {
                    Text(NSLocalizedString("menu_reset", comment: "Reset API key"))
                }
            } label: {
                Image(systemName: "ellipsis")
            }
        }
    }
}

struct AlarmListContent: View {
    var alarms: [Alarm]
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    @State var selectedRow: Alarm?

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(alarms, id: \.id) { alarm in
                AlarmItem(
                    alarm: alarm,
                    onDelete: onDelete,
                    onUpdateDisabled: onUpdateDisabled,
                    selectedRow: $selectedRow
                )
                Divider()
                    .frame(height: 2)
                    .background(Color(.systemGray4))
            }
        }
        .frame(minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct AlarmItem: View {
    var alarm: Alarm
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    @Binding var selectedRow: Alarm?
    @State var disabled: Bool

    func isExpanded() -> Bool {
        alarm == selectedRow
    }

    init(alarm: Alarm, onDelete: @escaping (Int64) -> Void, onUpdateDisabled: @escaping (Int64, Bool) -> Void, selectedRow: Binding<Alarm?>) {
        self.alarm = alarm
        self.onDelete = onDelete
        self.onUpdateDisabled = onUpdateDisabled
        _selectedRow = selectedRow
        disabled = (alarm.disabled ?? false) as! Bool
    }

    func toggleDisabled() {
        disabled.toggle()
        onUpdateDisabled(alarm.id, disabled)
        withAnimation {
            selectedRow = nil
        }
    }

    func labelText(_ text: String) -> some View {
        Text(text)
            .foregroundColor(.labelColor)
            .font(.appFont(size: 14))
    }

    var body: some View {
        let backgroundColor = isExpanded() ? Color.selectedColor : Color.backgroundColor
        return VStack {
            HStack {
                VStack(alignment: .leading) {
                    labelText(NSLocalizedString("company_label", comment: "Company name"))
                    Text(alarm.insName)
                }
                Spacer()
                HStack {
                    VStack(alignment: .leading) {
                        labelText(NSLocalizedString("kpi_label", comment: "KPI Name"))
                        Text(alarm.kpiName)
                            .fixedSize(horizontal: false, vertical: true)
                            .lineLimit(2)
                    }
                    Spacer()
                    VStack(alignment: .trailing) {
                        labelText(NSLocalizedString("kpi_value", comment: "KPI Value"))
                        Text(String(format: "%.1f", alarm.kpiValue))
                    }
                }
                .frame(width: 150)
            }
            .contentShape(Rectangle())
            .onTapGesture {
                withAnimation {
                    selectedRow = selectedRow == alarm ? nil : alarm
                }
            }
            .frame(height: 50)
            if isExpanded() {
                HStack {
                    NavigationLink(destination: NavigationLazyView(EditView(alarm: alarm))) {
                        HStack(spacing: 0) {
                            Image(systemName: "pencil")
                                .padding(.trailing, 4)
                            Text(NSLocalizedString("list_edit_button", comment: "Edit Alarm"))
                        }
                    }
                    Spacer()
                    Button(action: toggleDisabled) {
                        let (icon, text) = disabled
                            ? ("bell", NSLocalizedString("list_reactivate_button", comment: "Reactivate alarm"))
                            : ("bell.slash", NSLocalizedString("list_deactivate_button", comment: "Deactivate Alarm"))
                        HStack(spacing: 0) {
                            Image(systemName: icon)
                                .padding(.trailing, 4)
                            Text(text)
                        }
                    }
                    Button(action: {
                        onDelete(alarm.id)
                    }, label: {
                        HStack(spacing: 0) {
                            Image(systemName: "trash")
                                .padding(.trailing, 4)
                            Text(NSLocalizedString("list_delete_button", comment: "Delete Alarm"))
                        }
                    })
                    .padding(.leading, 8)
                }
                .foregroundColor(.textColor)
                .padding(.vertical, 4)
            }
        }
        .onAppear {
            selectedRow = nil
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(backgroundColor)
        .opacity(disabled ? 0.38 : 1.0)
    }
}

struct WelcomeInfoContent: View {
    var body: some View {
        VStack(alignment: .center, spacing: 0) {
            Group {
                Text(NSLocalizedString("welcome_p1", comment: "First paragraph"))
                Text(NSLocalizedString("welcome_p2", comment: "Second paragraph"))

                Text(NSLocalizedString("welcome_p3_1", comment: "Third paragraph part 1"))
                    + Text(Image(systemName: "bolt.fill"))
                    + Text(NSLocalizedString("welcome_p3_2", comment: "Third paragraph part 2"))

                Text(NSLocalizedString("welcome_p4", comment: "Fourth and final paragraph"))
            }
            .multilineTextAlignment(.center)
            .padding(8)
        }
        .frame(minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
        .padding()
    }
}

struct ListView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            ListViewContent(
                loading: false,
                alarms: [
                    Alarm(
                        id: 1,
                        insId: 1,
                        insName: "Evolution",
                        yahooId: "1",
                        kpiId: 1,
                        kpiName: "P/E",
                        kpiValue: 30.0,
                        operation: "lt",
                        disabled: false
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
                        disabled: true
                    ),
                ],
                onDelete: { _ in },
                onUpdateDisabled: { _, _ in },
                onResetKey: {}
            )
        }
        .previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
