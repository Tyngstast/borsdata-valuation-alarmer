package com.github.tyngstast.borsdatavaluationalarmer.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.tyngstast.borsdatavaluationalarmer.BorsdataApi
import com.github.tyngstast.borsdatavaluationalarmer.Dao
import com.github.tyngstast.borsdatavaluationalarmer.DatabaseDriverFactory
import com.github.tyngstast.db.Alarm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {
    companion object {
        private val log = Logger.getLogger("main")
    }

    private val mainScope = MainScope()

    private val dao = Dao(DatabaseDriverFactory(this))
    private val borsdataApi: BorsdataApi = BorsdataApi()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Loading..."

        // lookup from borsdata API
        mainScope.launch {
            kotlin.runCatching {
                val alarms = dao.getAllAlarms()
                log.info("alarms: $alarms")

                val first: Alarm

                if (alarms.isEmpty()) {
                    dao.insertAlarm(750, "Evolution", 2, 40.0)
                    first = dao.getAllAlarms()[0]
                } else {
                    first = alarms[0]
                }

                log.info("first: $first")

                val response = borsdataApi.getLatestValue(first.insId, first.kpiId, "redacted")
                log.info("response from bd: $response")

                first to response
            }.onSuccess { (alarm, kpi) ->
                tv.text = "${alarm.insName} current P/E: ${kpi.value.n}"
            }.onFailure {
                tv.text = "Integration error: $it"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
