import SwiftUI

struct NavigationBarModifier: ViewModifier {
    var titleFont: UIFont?
    var backgroundColor: UIColor?
    var titleColor: UIColor?

    init(titleFont: UIFont?, backgroundColor: UIColor?, titleColor: UIColor?) {
        self.backgroundColor = backgroundColor
        
        let coloredAppearance = UINavigationBarAppearance()
        coloredAppearance.configureWithTransparentBackground()
        coloredAppearance.backgroundColor = backgroundColor
        
        let font = titleFont ?? UIFont.systemFont(ofSize: 24)
        let largeTitleFont = UIFontMetrics(forTextStyle: .largeTitle).scaledFont(for: font)
        coloredAppearance.largeTitleTextAttributes = [
            .font: largeTitleFont,
            .foregroundColor: titleColor ?? .white
        ]
        
        let titleFont = UIFontMetrics(forTextStyle: .headline).scaledFont(for: font)
        coloredAppearance.titleTextAttributes = [
            .font: titleFont,
            .foregroundColor: titleColor ?? .white
        ]

        UINavigationBar.appearance().standardAppearance = coloredAppearance
        UINavigationBar.appearance().compactAppearance = coloredAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = coloredAppearance
    }

    func body(content: Content) -> some View {
        ZStack{
            content
            VStack {
                GeometryReader { geometry in
                    Color(self.backgroundColor ?? .clear)
                        .frame(height: geometry.safeAreaInsets.top)
                        .edgesIgnoringSafeArea(.top)
                    Spacer()
                }
            }
        }
    }
}

extension View {
    func navigationBarStyle(font: UIFont?, backgroundColor: UIColor?, titleColor: UIColor?) -> some View {
        self.modifier(NavigationBarModifier(titleFont: font, backgroundColor: backgroundColor, titleColor: titleColor))
    }
}
