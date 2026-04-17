package com.tastyhome.base.platform.notification

import com.tastyhome.base.platform.permissions.Permission
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.permissions.PermissionStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.random.Random

actual class NotificationTapAction(val userInfo: Map<Any?, *>)

private const val INTERVAL = 0.1

internal class IosNotificationManager(
    private val permissionManager: PermissionManager
) : NotificationManager {

    private val center: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun showNotification(
        title: String,
        message: String,
        channelId: String,
        notificationId: Int?,
        tapAction: NotificationTapAction?,
    ) = suspendCancellableCoroutine { cont ->
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setThreadIdentifier(channelId)
            tapAction?.userInfo?.let { setUserInfo(it) }
        }
        val identifier = notificationId?.toString() ?: "${Random.nextInt()}"
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(INTERVAL, false)
        val request = UNNotificationRequest.requestWithIdentifier(identifier, content, trigger)
        center.addNotificationRequest(request) { error ->
            cont.resume(Unit)
        }
    }

    override suspend fun cancelNotification(notificationId: Int) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId.toString()))
        center.removeDeliveredNotificationsWithIdentifiers(listOf(notificationId.toString()))
    }

    override suspend fun requestPermission(): Boolean =
        permissionManager.requestPermission(Permission.NOTIFICATION) == PermissionStatus.GRANTED
}
