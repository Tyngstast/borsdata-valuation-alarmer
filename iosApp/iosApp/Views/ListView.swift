import SwiftUI
import shared

struct ListView: View {
    @ObservedObject var viewModel = AlarmListViewModel()
    
    var body: some View {
        ListViewContent(
            loading: viewModel.loading,
            alarms: viewModel.alarms
        )
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
        .frame(maxHeight: .infinity, alignment: .topLeading)
        .padding()
    }
}

struct AlarmListContent: View {
    var alarms: [Alarm]
    
    var body: some View {
        Text("List")
    }
}

struct WelcomeInfoContent: View {
    var body: some View {
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
}

struct ListView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            ListViewContent(
                loading: false,
                alarms: []
            )
        }.previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
