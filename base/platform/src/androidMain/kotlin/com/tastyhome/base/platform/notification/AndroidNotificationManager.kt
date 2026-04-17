package com.tastyhome.base.platform.notification

import android.app.NotificationChannel
import android.app.NotificationManager as SystemNotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tastyhome.base.platform.ActivityHolder
import com.tastyhome.base.platform.permissions.Permission
import com.tastyhome.base.platform.permissions.PermissionManager
import com.tastyhome.base.platform.permissions.PermissionStatus
import kotlin.random.Random

actual class NotificationTapAction(val intent: Intent)

internal class AndroidNotificationManager(
    private val activityHolder: ActivityHolder,
    private val context: Context,
    private val permissionManager: PermissionManager,
    private val iconRes: Int,
) : NotificationManager {

    private val systemNotificationManager: SystemNotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as SystemNotificationManager
    }

    override suspend fun showNotification(
        title: String,
        message: String,
        channelId: String,
        notificationId: Int?,
        tapAction: NotificationTapAction?,
    ) {
        systemNotificationManager.getNotificationChannel(channelId)
            ?: systemNotificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelId,
                    SystemNotificationManager.IMPORTANCE_DEFAULT
                )
            )
        val id = notificationId ?: (Random.nextInt())
        val intent = tapAction?.intent
            ?: activityHolder.activity?.let { Intent(context, it::class.java) }
            ?: context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        )
        val pendingIntent = PendingIntent.getActivity(context, id, intent, FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        systemNotificationManager.notify(id, notification)
    }

    override suspend fun cancelNotification(notificationId: Int) {
        systemNotificationManager.cancel(notificationId)
    }

    override suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
        return permissionManager.requestPermission(Permission.NOTIFICATION) == PermissionStatus.GRANTED
    }
}
