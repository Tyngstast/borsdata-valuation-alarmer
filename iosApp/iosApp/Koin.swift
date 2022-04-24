import Foundation
import shared

func startKoin() {
    let userDefaults = UserDefaults(suiteName: "VALUATION_ALARMER_SETTINGS")!

    let koinApplication = IOSKoinKt.doInitKoinIos(
        userDefaults: userDefaults
    )
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin?
var koin: Koin_coreKoin {
    return _koin!
}
