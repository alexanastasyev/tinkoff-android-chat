package com.example.myapplication

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class AsyncService(name: String = "myService") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        Log.i("MyResult", "IntentService started")

        // TODO("Long operation")

        Log.i("MyResult", "IntentService is working...")

        // This should be the result of the long operation above
        val result = "Fake result"

        sendResultToBroadcastReceiver(result)
    }

    private fun sendResultToBroadcastReceiver(result: String) {
        val actionName = getString(R.string.my_action_name)
        val resultKey = getString(R.string.result_key)
        val intent = Intent(actionName).putExtra(resultKey, result)
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        localBroadcastReceiver.sendBroadcast(intent)
        Log.i("MyResult", "IntentService sent intent to broadcast")
    }
}