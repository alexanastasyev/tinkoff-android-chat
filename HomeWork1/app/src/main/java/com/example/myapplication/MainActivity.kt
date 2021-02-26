package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest.permission.READ_CONTACTS
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_FOR_ACTIVITY = 100
        private const val REQUEST_CODE_FOR_PERMISSION = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestContactsPermissionAndStartSecondActivity()
    }

    private fun requestContactsPermissionAndStartSecondActivity() {
        // Check the SDK version and whether the permission is already granted or not.
        // TODO("Ask mentor how to put it into function")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_CODE_FOR_PERMISSION)
        } else {
            startSecondActivity()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_FOR_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSecondActivity()
            }
        }
    }

    private fun startSecondActivity() {
        intent = Intent(this, SecondActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_FOR_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result = getResultStringFromIntent(data)
            setResultInUi(result)
        }
    }

    private fun setResultInUi(result: ArrayList<String>?) {
       if (result != null) {
           val textView : TextView = findViewById(R.id.textView)
           textView.text = result.toString()
       }
    }

    private fun getResultStringFromIntent(intent: Intent?) : ArrayList<String>? {
        val resultKey = getString(R.string.result_key)
        return intent?.getStringArrayListExtra(resultKey)
    }

}