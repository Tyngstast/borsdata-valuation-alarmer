import SwiftUI
import shared

struct ListView: View {
    @ObservedObject var viewModel = AlarmListViewModel()
    
    func onEdit(id: Int64) {
        
    }
    
    var body: some View {
        ListViewContent(
            loading: viewModel.loading,
            alarms: viewModel.alarms,
            onEdit: onEdit,
            onDelete: viewModel.deleteAlarm,
            onUpdateDisabled: viewModel.updateDisabled
        )
        .onAppear(perform: viewModel.activate)
        .onDisappear(perform: viewModel.deactivate)
    }
}

struct ListViewContent: View {
    var loading: Bool
    var alarms: [Alarm]
    var onEdit: (Int64) -> Void
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    
    var body: some View {
        VStack {
            if !alarms.isEmpty {
                AlarmListContent(
                    alarms: alarms,
                    onEdit: onEdit,
                    onDelete: onDelete,
                    onUpdateDisabled: onUpdateDisabled
                )
            } else if !loading {
                WelcomeInfoContent()
            }
        }
        .navigationTitle(NSLocalizedString("list_text_title", comment: "List alarms title"))
    }
}

struct AlarmListContent: View {
    var alarms: [Alarm]
    var onEdit: (Int64) -> Void
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(alarms, id: \.id) { alarm in
                AlarmItem(
                    alarm: alarm,
                    onEdit: onEdit,
                    onDelete: onDelete,
                    onUpdateDisabled: onUpdateDisabled
                )
                Divider()
                    .frame(height: 2)
                    .background(Color(.systemGray5))
            }
        }
        .frame(minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct AlarmItem: View {
    var alarm: Alarm
    var onEdit: (Int64) -> Void
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    @State private var isExpanded = false
    @State var disabled: Bool
    
    init(alarm: Alarm, onEdit: @escaping (Int64) -> Void, onDelete: @escaping (Int64) -> Void, onUpdateDisabled: @escaping (Int64, Bool) -> Void) {
        self.alarm = alarm
        self.onEdit = onEdit
        self.onDelete = onDelete
        self.onUpdateDisabled = onUpdateDisabled
        self.disabled = (alarm.disabled ?? false) as! Bool
    }
    
    func toggleDisabled() {
        onUpdateDisabled(alarm.id, disabled)
        disabled.toggle()
        withAnimation{
            isExpanded.toggle()
        }
    }
    
    var body: some View {
        let backgroundColor = isExpanded ? Color.selectedColor : Color.backgroundColor
        return VStack {
            HStack {
                VStack(alignment: .leading) {
                    Text(NSLocalizedString("company_label", comment: "Company name"))
                        .foregroundColor(Color(UIColor.darkGray))
                        .font(.appFont(size: 14))
                    Text(alarm.insName)
                }
                Spacer()
                VStack(alignment: .leading) {
                    Text(NSLocalizedString("kpi_label", comment: "KPI Name"))
                        .foregroundColor(Color(UIColor.darkGray))
                        .font(.appFont(size: 14))
                    Text(alarm.kpiName)
                }
                VStack(alignment: .trailing) {
                    Text(NSLocalizedString("kpi_value", comment: "KPI Value"))
                        .foregroundColor(Color(UIColor.darkGray))
                        .font(.appFont(size: 14))
                    Text(String(format: "%.1f", alarm.kpiValue))
                }
                .padding(.leading, 12)
            }
            .contentShape(Rectangle())
            .onTapGesture {
                withAnimation{
                    isExpanded.toggle()
                }
            }
            .frame(height: 50)
            if isExpanded {
                HStack {
                    Button(action: { onEdit(alarm.id) }) {
                        Text(Image(systemName: "pencil")) + Text(" \(NSLocalizedString("list_edit_button", comment: "Edit Alarm"))")
                    }
                    Spacer()
                    Button(action: toggleDisabled) {
                        let (icon, text) = disabled
                            ? ("bell", NSLocalizedString("list_reactivate_button", comment: "Reactivate alarm"))
                            : ("bell.slash", NSLocalizedString("list_deactivate_button", comment: "Deactivate Alarm"))
                        Text(Image(systemName: icon)) + Text(" \(text)")
                    }
                    Button(action: { onDelete(alarm.id) }) {
                        Text(Image(systemName: "trash")) + Text(" \(NSLocalizedString("list_delete_button", comment: "Delete Alarm"))")
                    }
                    .padding(.leading, 8)
                }
                .foregroundColor(.textColor)
                .padding(.vertical, 4)
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(backgroundColor)
    }
}

struct WelcomeInfoContent: View {
    var body: some View {
        VStack {
             Group {
                Text(NSLocalizedString("welcome_p1", comment: "First paragraph"))
                Text(NSLocalizedString("welcome_p2", comment: "Second paragraph"))
                (
                    Text(NSLocalizedString("welcome_p3_1", comment: "Third paragraph part 1"))
                    + Text(Image(systemName: "bolt.fill"))
                    + Text(NSLocalizedString("welcome_p3_2", comment: "Third paragraph part 2"))
                )
                Text(NSLocalizedString("welcome_p4", comment: "Fourth and final paragraph"))
            }
            .multilineTextAlignment(.center)
            .padding(8)
        }
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
                        disabled: false
                    )
                ],
                onEdit: { _ in },
                onDelete: { _ in },
                onUpdateDisabled: { _, _ in }
            )
        }.previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
