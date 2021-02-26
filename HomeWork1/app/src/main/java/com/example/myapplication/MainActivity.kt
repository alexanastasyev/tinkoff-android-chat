package com.example.myapplication

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("MyResult", "First activity started")

        startSecondActivity()
    }

    private fun startSecondActivity() {
        intent = Intent(this, SecondActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            Log.i("MyResult", "First activity got result from second activity")

            val result = data?.getIntExtra("res", 0)

            Log.i("MyResult", "Result: $result")

        }
    }

}