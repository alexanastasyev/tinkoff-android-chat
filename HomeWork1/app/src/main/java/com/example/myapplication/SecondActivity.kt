package com.example.myapplication

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.ArrayList

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        startIntentService()
    }

    private fun startIntentService() {
        val intent = Intent(this, AsyncService::class.java)
        startService(intent)
    }

    override fun onStart() {
        super.onStart()
        makeFilterAndRegisterBroadcastReceiver()
    }

    private fun makeFilterAndRegisterBroadcastReceiver() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        val actionName = getString(R.string.my_action_name)
        val filter = IntentFilter(actionName)
        localBroadcastReceiver.registerReceiver(broadcastReceiver, filter)
    }

    override fun onStop() {
        unregisterBroadcastReceiver()
        super.onStop()
    }

    private fun unregisterBroadcastReceiver() {
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        localBroadcastReceiver.unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            saveResultAndFinishActivity(intent)
        }

        private fun saveResultAndFinishActivity(intent: Intent?) {
            val result = getStringFromIntent(intent)
            saveResult(result)
            finish()
        }

        private fun getStringFromIntent(intent: Intent?): ArrayList<String>? {
            return intent?.getStringArrayListExtra(getString(R.string.result_key))
        }

        private fun saveResult(result: ArrayList<String>?) {
            val intent = Intent().putExtra(getString(R.string.result_key), result)
            setResult(Activity.RESULT_OK, intent)
        }
    }
}

