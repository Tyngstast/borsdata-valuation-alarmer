import SwiftUI
import shared

struct ListView: View {
    @ObservedObject var viewModel = AlarmListViewModel()
    
    var body: some View {
        ListViewContent(
            loading: viewModel.loading,
            alarms: viewModel.alarms
        )
        .onAppear(perform: viewModel.activate)
        .onDisappear(perform: viewModel.deactivate)
    }
}

struct ListViewContent: View {
    var loading: Bool
    var alarms: [Alarm]
    
    var body: some View {
        VStack {
            if !alarms.isEmpty {
                AlarmListContent(alarms: alarms)
            } else if !loading {
                WelcomeInfoContent()
            }
        }
        .navigationTitle(NSLocalizedString("list_text_title", comment: "List alarms title"))
    }
}

struct AlarmListContent: View {
    var alarms: [Alarm]
    
    var body: some View {
        VStack(alignment: .leading) {
            ForEach(alarms, id: \.id) { alarm in
                AlarmItem(alarm: alarm)
                    .padding(.horizontal)
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
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(NSLocalizedString("company_label", comment: "Company name"))
                    .foregroundColor(Color(UIColor.darkGray))
                Text(alarm.insName)
            }
            Spacer()
            VStack(alignment: .leading) {
                Text(NSLocalizedString("kpi_label", comment: "KPI Name"))
                    .foregroundColor(Color(UIColor.darkGray))
                Text(alarm.kpiName)
            }
            VStack(alignment: .trailing) {
                Text(NSLocalizedString("kpi_value", comment: "KPI Value"))
                    .foregroundColor(Color(UIColor.darkGray))
                Text(String(format: "%.1f", alarm.kpiValue))
            }
            .padding(.leading, 12)
        }
        .frame(height: 50)
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
                ]
            )
        }.previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
