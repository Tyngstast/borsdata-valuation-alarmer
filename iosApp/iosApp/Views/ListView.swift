import SwiftUI

struct ListView: View {
    var body: some View {
        VStack {
            Text("List")
        }.navigationTitle(NSLocalizedString("list_text_title", comment: "List alarms title"))
    }
}


struct ListView_Previews: PreviewProvider {
    static var previews: some View {
        ListView()
    }
}
