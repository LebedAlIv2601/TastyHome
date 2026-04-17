package com.tastyhome.base.platform.notification

const val DEFAULT_NOTIFICATION_CHANNEL_ID = "Default"

expect class NotificationTapAction

interface NotificationManager {

    suspend fun showNotification(
        title: String,
        message: String,
        channelId: String = DEFAULT_NOTIFICATION_CHANNEL_ID,
        notificationId: Int? = null,
        tapAction: NotificationTapAction? = null,
    )

    suspend fun cancelNotification(notificationId: Int)

    suspend fun requestPermission(): Boolean
}
