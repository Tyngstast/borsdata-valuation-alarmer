import Foundation
import shared

func startKoin() {
    let userDefaults = UserDefaults(suiteName: "VALUATION_ALARMER_SETTINGS")!
    let langStr = Locale.autoupdatingCurrent.languageCode

    let koinApplication = IOSKoinKt.doInitKoinIos(
        userDefaults: userDefaults,
        langStr: langStr,
        appInfo: IosAppInfo()
    )
    _koin = koinApplication.koin
}

private var _koin: Koin_coreKoin?
var koin: Koin_coreKoin {
    _koin!
}

class IosAppInfo: AppInfo {
    let appVersion: Int32 = Int32(Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "0") ?? 0
}
