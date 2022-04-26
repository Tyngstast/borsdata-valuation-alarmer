import SwiftUI

extension String {
    static let appFont = "Montserrat-Regular"
}

extension Color {
    static let primaryColor = Color("Primary")
    static let secondaryColor = Color("Secondary")
    static let backgroundColor = Color("Background")
    static let textColor = Color("TextColor")
    static let errorColor = Color("Error")
    static let paleWhite = Color(white: 1, opacity: 179 / 255.0)
}

extension UIColor {
    static let primaryColor = UIColor(.primaryColor)
    static let secondaryColor = UIColor(.secondaryColor)
    static let backgroundColor = UIColor(.backgroundColor)
    static let textColor = UIColor(.textColor)
}

extension UIFont {
    static let navigationTitleFont = UIFont(name: .appFont, size: 24)
}

