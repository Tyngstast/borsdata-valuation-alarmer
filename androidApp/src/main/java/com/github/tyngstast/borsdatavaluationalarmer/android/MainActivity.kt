package com.github.tyngstast.borsdatavaluationalarmer.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.tyngstast.borsdatavaluationalarmer.Greeting
import android.widget.TextView
import com.github.tyngstast.borsdatavaluationalarmer.Dao
import com.github.tyngstast.borsdatavaluationalarmer.DatabaseDriverFactory
import java.util.logging.Logger

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {

    companion object {
        private val log = Logger.getLogger("main")
    }
    private val dao = Dao(DatabaseDriverFactory(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // insert evolution gaming P/E alarm
        dao.insertAlarm(750, "Evolution", 2, 40.0)

        val alarms = dao.getAllAlarms()
        log.info("alarms: $alarms")

        // lookup from borsdata API

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = greet()
    }
}
