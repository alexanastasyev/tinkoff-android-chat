package com.example.myapplication

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        Log.i("MyResult", "Second activity started")

        startIntentService()

        Log.i("MyResult", "Second activity sent intent to IntentService")
    }

    private fun startIntentService() {
        val intent = Intent(this, AsyncService::class.java)
        startService(intent)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        val actionName = getString(R.string.my_action_name)
        val filter = IntentFilter(actionName)
        localBroadcastReceiver.registerReceiver(myReceiver, filter)
    }

    override fun onStop() {
        unregisterBroadcastReceiver()
        super.onStop()
    }

    private fun unregisterBroadcastReceiver() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        localBroadcastReceiver.unregisterReceiver(myReceiver)
    }

    private var myReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("MyResult", "Broadcast Receiver received intent")

            saveResultAndFinishActivity(intent)
        }

        private fun saveResultAndFinishActivity(intent: Intent?) {

            val result = getResultStringFromIntent(intent)
            saveResult(result)
            finish()
        }

        private fun getResultStringFromIntent(intent: Intent?): String {
            return intent?.getStringExtra(getResultKeyFromResources()).toString()
        }

        private fun saveResult(result: String) {

            val intent = Intent().putExtra(getResultKeyFromResources(), result)

            Log.i("MyResult", "Broadcast Receiver finishes Second activity")

            setResult(Activity.RESULT_OK, intent)

        }
    }

    private fun getResultKeyFromResources(): String {
        return getString(R.string.result_key)
    }

}

