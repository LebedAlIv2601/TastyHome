package com.tastyhome.base.platform.systemNavigator

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.tastyhome.base.platform.ActivityHolder
import com.tastyhome.base.platform.appInfo.ApplicationInfoManager
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.file.image.Image
import com.tastyhome.base.platform.utils.shareIntent
import com.tastyhome.base.platform.utils.startIntentSafe

private const val GOOGLE_PLAY_WEB_PREFIX = "https://play.google.com/store/apps/details?id="

internal class AndroidSystemNavigator(
    private val activityHolder: ActivityHolder,
    private val fileManager: FileManager,
    private val flavorInfoManager: ApplicationInfoManager,
) : SystemNavigator {

    private val context: Context? get() = activityHolder.activity

    override fun openSettings() {
        val context = context ?: return
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startIntentSafe(intent)
    }

    override fun openAppSettings() {
        val context = context ?: return
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startIntentSafe(intent)
    }

    override fun openMarket(packageNameOrAppId: String, onFailure: (Throwable) -> Unit) {
        val context = context ?: return
        val marketUrl = flavorInfoManager.source.baseUrl + packageNameOrAppId
        val marketIntent = Intent(Intent.ACTION_VIEW).apply {
            data = marketUrl.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
            data = (GOOGLE_PLAY_WEB_PREFIX + packageNameOrAppId).toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (marketIntent.resolveActivity(context.packageManager) != null) {
            context.startIntentSafe(marketIntent) {
                context.startIntentSafe(fallbackIntent, onFailure)
            }
        } else {
            context.startIntentSafe(fallbackIntent, onFailure)
        }
    }

    override fun openPhoneDialer(phoneNumber: String, onFailure: (Throwable) -> Unit) {
        val context = context ?: return
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            onFailure(IllegalStateException("Device has no telephony feature"))
            return
        }
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startIntentSafe(intent, onFailure)
    }

    override fun openBrowser(url: String, onFailure: (Throwable) -> Unit) {
        val context = context ?: return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = if (url.startsWith("http")) url.toUri() else "https://$url".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startIntentSafe(intent, onFailure)
    }

    override suspend fun share(text: String, title: String?, onFailure: (Throwable) -> Unit) {
        val context = context ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            if (!title.isNullOrEmpty()) {
                putExtra(Intent.EXTRA_TITLE, title)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, title ?: "")
        context.startIntentSafe(chooser, onFailure)
    }

    override suspend fun shareImage(image: Image, title: String?, onFailure: (Throwable) -> Unit) {
        val context = context ?: return
        if (!image.exists()) {
            onFailure(IllegalStateException("Image file does not exist: ${image.getPath()}"))
            return
        }
        val uri = FileProvider.getUriForFile(
            context,
            context.getFileProviderAuthority(),
            image.file,
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, title ?: "Share image")
        context.startIntentSafe(chooser, onFailure)
    }

    override suspend fun shareFile(byteArray: ByteArray, type: String, name: String, onFailure: (Throwable) -> Unit) {
        val context = context ?: return
        try {
            val file = fileManager.createFileFromBytes(name, byteArray)
            val uri = FileProvider.getUriForFile(
                context,
                context.getFileProviderAuthority(),
                file.file,
            )
            context.shareIntent(uri, type, onFailure)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun Context.getFileProviderAuthority(): String = "$packageName.fileprovider"
}
