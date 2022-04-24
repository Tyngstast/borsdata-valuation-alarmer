import SwiftUI

struct ListView: View {
    var body: some View {
        VStack {
            Text("List")
        }.navigationTitle("Active Alarms")
    }
}


struct ListView_Previews: PreviewProvider {
    static var previews: some View {
        ListView()
    }
}
