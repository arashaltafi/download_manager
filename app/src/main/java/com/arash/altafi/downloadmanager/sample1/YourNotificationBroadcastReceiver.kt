package com.arash.altafi.downloadmanager.sample1

import android.content.Context
import android.content.Intent
import ir.siaray.downloadmanagerplus.receivers.NotificationBroadcastReceiver

class YourNotificationBroadcastReceiver : NotificationBroadcastReceiver() {
    override fun onCompleted(context: Context?, intent: Intent?, downloadId: Long) {
        super.onCompleted(context, intent, downloadId)
    }

    override fun onClicked(context: Context?, intent: Intent?, downloadIdList: LongArray?) {
        super.onClicked(context, intent, downloadIdList)
    }

    override fun onFailed(context: Context?, intent: Intent?, downloadId: Long) {
        super.onFailed(context, intent, downloadId)
    }
}