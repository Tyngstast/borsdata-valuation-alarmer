package com.github.tyngstast.borsdatavaluationalarmer.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.work.*
import com.github.tyngstast.borsdatavaluationalarmer.Dao
import com.github.tyngstast.borsdatavaluationalarmer.DatabaseDriverFactory
import com.github.tyngstast.borsdatavaluationalarmer.KVaultFactory
import com.github.tyngstast.borsdatavaluationalarmer.KVaultImpl
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.ValuationAlarmDataFetcherWorker
import com.github.tyngstast.borsdatavaluationalarmer.android.worker.ValuationAlarmNotificationWorker
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity";
        private const val VALUATION_SYNC_WORK = "valuation_sync_work";
    }

    private val mainScope = MainScope()
    private val workManager = WorkManager.getInstance(this)
    private val dao = Dao(DatabaseDriverFactory(this))
    private val lazyKVaultImpl = lazy {
        KVaultImpl(KVaultFactory(this))
    }

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
        val apiKey = lazyKVaultImpl.value.getApiKey()
        Log.i(TAG, "key: $apiKey")

        if (apiKey.isNullOrBlank()) {
            lazyKVaultImpl.value.setApiKey("redacted")
        }

        val alarms = dao.getAllAlarms()
        if (alarms.isEmpty()) {
            dao.insertAlarm(750, "Evolution", 2, "P/E", 40.0, "lte")
            dao.insertAlarm(408, "Kambi", 2, "P/E", 30.0, "lte")
        }

        Log.i(TAG, "alarms: ${dao.getAllAlarms()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
