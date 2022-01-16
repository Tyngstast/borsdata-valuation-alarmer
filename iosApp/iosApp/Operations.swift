import Foundation
import shared

class ValuartionAlarmOperation: Operation {
    
    let dao = Dao(databaseDriverFactory: DatabaseDriverFactory())
    let api = BorsdataApi(kVaultImpl: KVaultImpl(kVaultFactory: KVaultFactory()))
    
    override func main() {
        print("operation main")
    }
}
