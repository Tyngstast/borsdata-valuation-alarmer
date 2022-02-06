import Foundation
import shared

class ValuartionAlarmOperation: Operation {
    
    let dao = Dao(databaseDriverFactory: DatabaseDriverFactory())
    let api = BorsdataClient(kVaultImpl: KVaultImpl(kVaultFactory: KVaultFactory()))
    
    override func main() {
        print("operation main")
    }
}
