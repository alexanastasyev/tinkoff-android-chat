package com.example.myapplication

import android.app.IntentService
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.Manifest.permission.READ_CONTACTS

class AsyncService(name: String = "myService") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        val result = checkPermissionAndGetContacts()
        sendResultToBroadcastReceiver(result)
    }


    private fun checkPermissionAndGetContacts() : ArrayList<String> {
        var contacts = arrayListOf<String>()

        // Check the SDK version and whether the permission is already granted or not.
        // TODO("Ask mentor how to put it into function")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contacts = getContacts()
        }
        return contacts
    }

    private fun getContacts() : ArrayList<String> {
        val contentResolver = contentResolver
        val people: Cursor? = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val contacts = arrayListOf<String>()
        if (people != null) {
            val nameIndex: Int = people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (people.moveToNext()) {
                val name = people.getString(nameIndex)
                contacts.add(name)
            }
        }
        people?.close()
        return contacts
    }

    private fun sendResultToBroadcastReceiver(result: ArrayList<String>) {
        val actionName = getString(R.string.my_action_name)
        val resultKey = getString(R.string.result_key)
        val intent = Intent(actionName).putExtra(resultKey, result)
        val localBroadcastReceiver = LocalBroadcastManager.getInstance(this)
        localBroadcastReceiver.sendBroadcast(intent)
    }
}