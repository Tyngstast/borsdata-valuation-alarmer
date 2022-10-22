import SwiftUI

extension String {
    static let appFont = "Montserrat-Regular"
}

extension Font {
    static func appFont(size: CGFloat = 17) -> Font {
        return Font.custom(.appFont, size: size)
    }
}

extension Color {
    static let primaryColor = Color("Primary")
    static let secondaryColor = Color("Secondary")
    static let highlightColor = Color("Highlight")
    static let foregroundItemColor = Color("ForegroundItem")
    static let labelColor = Color("LabelColor")
    static let backgroundColor = Color("Background")
    static let selectedColor = Color("SelectedRow")
    static let textColor = Color("TextColor")
    static let errorColor = Color("Error")
    static let paleWhite = Color(white: 1, opacity: 179 / 255.0)
}

extension UIColor {
    static let primaryColor = UIColor(.primaryColor)
    static let secondaryColor = UIColor(.secondaryColor)
    static let highlightColor = UIColor(.highlightColor)
    static let backgroundColor = UIColor(.backgroundColor)
    static let textColor = UIColor(.textColor)
}

extension ShapeStyle where Self == Color {
    static var backgrounShapeStyle: Color {
        .backgroundColor
    }
}

extension UIFont {
    static let navigationTitleFont = UIFont(name: .appFont, size: 24)
}

