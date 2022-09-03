import shared
import SwiftUI

struct EditView: View {
    var alarm: Alarm?

    init(id _: Int64) {
        print("todo: get alarm from id as pass to content")
    }

    var body: some View {
        EditViewContent()
    }
}

struct EditViewContent: View {
    var body: some View {
        VStack {
            Text("Edit")
        }
        .navigationTitle(NSLocalizedString("edit_text_title", comment: "Edit alarm title"))
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct EditView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            EditViewContent()
        }.previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
