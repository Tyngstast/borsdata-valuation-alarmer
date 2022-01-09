package com.github.tyngstast.borsdatavaluationalarmer.android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.tyngstast.borsdatavaluationalarmer.BorsdataApi
import com.github.tyngstast.borsdatavaluationalarmer.Dao
import com.github.tyngstast.borsdatavaluationalarmer.DatabaseDriverFactory
import com.github.tyngstast.borsdatavaluationalarmer.Greeting
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.logging.Logger

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    companion object {
        private val log = Logger.getLogger("main")
    }

    private val mainScope = MainScope()

    private val dao = Dao(DatabaseDriverFactory(this))
    private val borsdataApi: BorsdataApi = BorsdataApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()

        // lookup from borsdata API
        mainScope.launch {
            // insert evolution gaming P/E alarm
            dao.insertAlarm(750, "Evolution", 2, 40.0)

            val alarms = dao.getAllAlarms()
            log.info("alarms: $alarms")

            val first = alarms[0]
            log.info("first: $first")

            val response = borsdataApi.getLatestValue(first.insId, first.kpiId, "GET_KEY")
            log.info("response from bd: $response")
        }
    }
}
