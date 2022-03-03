package com.stark.emoji_status_app.ui

import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stark.emoji_status_app.createToast
import com.stark.emoji_status_app.network_handler.NETWORK
import com.stark.emoji_status_app.network_handler.NetworkLiveData
import com.stark.emoji_status_app.network_handler.NetworkService

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var broadcastReceiver: NetworkService

    var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::broadcastReceiver.isInitialized) {
            registerBroadcast()
        }
        observer()
    }

    private fun registerBroadcast() {
        val connectivityIntent = IntentFilter()
        connectivityIntent.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        broadcastReceiver = NetworkService()
        registerReceiver(broadcastReceiver, connectivityIntent)
    }

    private fun observer() {
        NetworkLiveData.observe(this) {
            when(it) {
                NETWORK.NOT_CONNECTED -> {
                    createToast("Network Not Available", Toast.LENGTH_SHORT).show()
                    isConnected = false
                }

                NETWORK.CONNECTED -> {
                    createToast("Network Connected", Toast.LENGTH_SHORT).show()
                    isConnected = true
                }

                else -> {
                    createToast("Something wrong with network", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}