import SwiftUI
import shared

struct ContentView: View {
    let dao = Dao(databaseDriverFactory: DatabaseDriverFactory())
    let borsdataApi = BorsdataApi()
    
    @State var message = "Loading..."
    
    func load() {
        let res = dao.getAllAlarms()
        print("all:", res)
        
        let first: Alarm
        
        if (res.isEmpty) {
            dao.insertAlarm(insId: 750, insName: "Evolution", kpiId: 2, value: 40.0)
            first = dao.getAllAlarms()[0]
        } else {
            first = res[0]
        }
        
        print("first:", first)
        
        borsdataApi.getLatestValue(insId: first.insId, kpiId: first.kpiId, authKey: "redacted") { result, error in
            if let result = result {
                print(result)
                self.message = "\(first.insName) current P/E \(result.value.n)"
            } else if let error = error {
                self.message = "Integration error: \(error)"
            }
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
