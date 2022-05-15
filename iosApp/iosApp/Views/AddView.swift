import shared
import SwiftUI

struct AddView: View {
    
    var body: some View {
        AddViewContent()
    }
}

struct AddViewContent: View {
    
    var body: some View {
        VStack {
            Text("Add")
        }
        .navigationTitle(NSLocalizedString("add_text_title", comment: "Add alarm title"))
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity, alignment: .topLeading)
    }
}

struct AddView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            AddViewContent()
        }.previewDevice(PreviewDevice(rawValue: "iPhone 11"))
    }
}
