import shared
import SwiftUI

struct InputField: View {
    var label: String
    @Binding var value: String
    var onInputChange: (String) -> Void
    var setFocus: () -> Void
    var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            TextField(label, text: $value)
                .onChange(of: value, perform: onInputChange)
                .textFieldStyle(InputFieldStyle())
            InputFieldDivider(isFocused: isFocused)
        }
        .disableAutocorrection(true)
        .onTapGesture(perform: setFocus)
    }
}

struct InputFieldDivider: View {
    var isFocused: Bool

    var body: some View {
        Divider()
            .frame(height: isFocused ? 2.5 : 2)
            .background(Color(isFocused ? .primaryColor : .systemGray5))
    }
}

struct InputFieldStyle: TextFieldStyle {
    func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .frame(minHeight: 50)
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
    }
}
