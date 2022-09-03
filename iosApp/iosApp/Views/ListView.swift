import shared
import SwiftUI

struct ListView: View {
    @StateObject var viewModel = AlarmListViewModel()
    var onResetKey: () -> Void

    var body: some View {
        ListViewContent(
            loading: viewModel.loading,
            alarms: viewModel.alarms,
            onDelete: viewModel.deleteAlarm,
            onUpdateDisabled: viewModel.updateDisabled,
            onResetKey: onResetKey
        )
        .onAppear(perform: viewModel.activate)
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
                    NavigationLink(destination: AddView()) {
                        Text("+")
                            .font(.system(.largeTitle))
                            .frame(width: 66, height: 62)
                            .foregroundColor(.black)
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

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ForEach(alarms, id: \.id) { alarm in
                AlarmItem(
                    alarm: alarm,
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
    var onDelete: (Int64) -> Void
    var onUpdateDisabled: (Int64, Bool) -> Void
    @State private var isExpanded = false
    @State var disabled: Bool

    init(alarm: Alarm, onDelete: @escaping (Int64) -> Void, onUpdateDisabled: @escaping (Int64, Bool) -> Void) {
        self.alarm = alarm
        self.onDelete = onDelete
        self.onUpdateDisabled = onUpdateDisabled
        disabled = (alarm.disabled ?? false) as! Bool
    }

    func toggleDisabled() {
        disabled.toggle()
        onUpdateDisabled(alarm.id, disabled)
        withAnimation {
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
                    withAnimation {
                        isExpanded.toggle()
                    }
                }
                .frame(height: 50)
            if isExpanded {
                HStack {
                    NavigationLink(destination: EditView(id: alarm.id)) {
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
        .padding(.horizontal)
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
