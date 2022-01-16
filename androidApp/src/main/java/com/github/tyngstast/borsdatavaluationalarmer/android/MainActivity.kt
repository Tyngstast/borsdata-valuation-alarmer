package com.github.tyngstast.borsdatavaluationalarmer.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.work.*
import com.github.tyngstast.borsdatavaluationalarmer.AlarmDao
import com.github.tyngstast.borsdatavaluationalarmer.Vault
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.ValuationAlarmDataFetcherWorker
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.ValuationAlarmNotificationWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MainActivity : ComponentActivity(), KoinComponent {
    companion object {
        private const val TAG = "MainActivity";
        private const val VALUATION_SYNC_WORK = "valuation_sync_work";
    }

    private val mainScope = MainScope()
    private val workManager = WorkManager.getInstance(this)
    private val alarmDao: AlarmDao by inject()
    private val vault: Vault by inject()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.initData()

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Loading..."

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workerRequest = OneTimeWorkRequestBuilder<ValuationAlarmDataFetcherWorker>()
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            VALUATION_SYNC_WORK,
            ExistingWorkPolicy.KEEP,
            workerRequest
        )
        .then(OneTimeWorkRequest.from(ValuationAlarmNotificationWorker::class.java))
        .enqueue()
    }

    private fun initData() {
        val apiKey = vault.getApiKey()
        Log.i(TAG, "key: $apiKey")

        if (apiKey.isNullOrBlank()) {
            vault.setApiKey("redacted")
        }

        val alarms = alarmDao.getAllAlarms()
        if (alarms.isEmpty()) {
            alarmDao.insertAlarm(750, "Evolution", 2, "P/E", 40.0, "lte")
            alarmDao.insertAlarm(408, "Kambi", 2, "P/E", 30.0, "lte")
        }

        Log.i(TAG, "alarms: ${alarmDao.getAllAlarms()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
