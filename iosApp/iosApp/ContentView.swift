import SwiftUI
import shared

struct ContentView: View {
    let dao = Dao(databaseDriverFactory: DatabaseDriverFactory())
    let borsdataClient = BorsdataClient(kVaultImpl: KVaultImpl(kVaultFactory: KVaultFactory()))
    
    @State var message = "Loading..."
    
    func load() {
        seed()
        
        let alarms = dao.getAllAlarms()
        print("alarms:", alarms)
        let first = alarms[0]
        
        borsdataClient.getLatestValue(insId: first.insId, kpiId: first.kpiId) { result, error in
            if let result = result {
                print(result)
                self.message = "\(first.insName) current P/E \(result.value.n)"
            } else if let error = error {
                print(error)
                self.message = "Integration error: \(error)"
            }
        }
    }
    
    private func seed() {
        let alarms = dao.getAllAlarms()
        
        if (alarms.isEmpty) {
            dao.insertAlarm(insId: 750, insName: "Evolution", kpiId: 2, kpiName: "P/E", kpiValue: 40.0, operation: "lte")
            dao.insertAlarm(insId: 408, insName: "Kambi", kpiId: 2, kpiName: "P/E", kpiValue: 30.0, operation: "lte")
        }
        
        let vault = KVaultImpl(kVaultFactory: KVaultFactory())
        guard let apiKey = vault.getApiKey(), !apiKey.isEmpty else {
            print("api key empty, seeding")
            vault.setApiKey(key: "redacted")
            return
        }
    }

	var body: some View {
        Text(message).onAppear {
            load()
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
