import Foundation
import shared

func startKoin() {
    let userDefaults = UserDefaults(suiteName: "VALUATION_ALARMER_SETTINGS")!
    let langStr = Locale.autoupdatingCurrent.languageCode

    let koinApplication = IOSKoinKt.doInitKoinIos(
        userDefaults: userDefaults,
        langStr: langStr
    )
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin?
var koin: Koin_coreKoin {
    return _koin!
}
