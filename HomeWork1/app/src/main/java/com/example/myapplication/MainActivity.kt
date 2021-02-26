package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest.permission.READ_CONTACTS
import android.widget.TextView
import com.example.myapplication.PermissionChecker.isPermissionGranted
import com.example.myapplication.PermissionChecker.isSdkVersionEnough
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_FOR_ACTIVITY = 100
        private const val REQUEST_CODE_FOR_PERMISSIONS = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestContactsPermissionAndStartSecondActivity()
    }

    private fun requestContactsPermissionAndStartSecondActivity() {
        if (isSdkVersionEnough()) {
            if (!isPermissionGranted(this, READ_CONTACTS)) {
                requestPermissions(arrayOf(READ_CONTACTS), REQUEST_CODE_FOR_PERMISSIONS)
            } else {
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
            val result = getResultFromIntent(data)
            setResultInUi(result)
        }
    }

    private fun getResultFromIntent(intent: Intent?): ArrayList<String>? {
        val resultKey = getString(R.string.result_key)
        return intent?.getStringArrayListExtra(resultKey)
    }

    private fun setResultInUi(result: ArrayList<String>?) {
        if (result != null) {

            val stringBuilder = StringBuilder()
            for (item in result) {
                stringBuilder.append(item).append("\n")
            }

            val formattedResult = stringBuilder.toString()

            val textView: TextView = findViewById(R.id.textView)
            textView.text = formattedResult
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_FOR_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSecondActivity()
            }
        }
    }
}