package com.stark.emoji_status_app.network_handler

import androidx.lifecycle.LiveData

object NetworkLiveData : LiveData<NETWORK>() {

    fun setNetworkStatus(status: NETWORK) {
        this.value = status
    }

}

enum class NETWORK {
    CONNECTED, NOT_CONNECTED
}
