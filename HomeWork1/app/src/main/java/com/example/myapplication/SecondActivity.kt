package com.example.myapplication

import android.app.Activity
import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class SecondActivity : AppCompatActivity() {

    var filter = IntentFilter("my.custom.ACTION")

    var myReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("MyResult", "Broadcast Receiver received intent")

            val result = intent?.getIntExtra("res", 0)
            val intent = Intent().putExtra("res", result)

            Log.i("MyResult", "Broadcast Receiver finishes Second activity")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        Log.i("MyResult", "Second activity started")

        val intent = Intent(this, AsyncService::class.java)
        startService(intent)

        Log.i("MyResult", "Second activity sent intent to IntentService")
    }

    override fun onStart() {
        super.onStart()
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        localBroadcastReceiver.registerReceiver(myReceiver, filter)
    }

    override fun onStop() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        localBroadcastReceiver.unregisterReceiver(myReceiver)
        super.onStop()
    }

    class AsyncService(name: String = "myService") : IntentService(name) {
        override fun onHandleIntent(intent: Intent?) {
            Log.i("MyResult", "IntentService started")

            // TODO("Long operation")
            val result = 12345

            val intent = Intent("my.custom.ACTION").putExtra("res", result)
            val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
            localBroadcastReceiver.sendBroadcast(intent)
            Log.i("MyResult", "IntentService sent intent to broadcast")
        }
    }

}