package com.tastyhome.base.platform.systemNavigator

import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.base.platform.appInfo.AppStore
import com.tastyhome.base.platform.appInfo.ApplicationInfoManager
import com.tastyhome.base.platform.file.FileManager
import com.tastyhome.base.platform.file.image.Image
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController

private const val APP_SETTINGS_URL = "app-settings:"
private const val APP_STORE_WEB_FALLBACK_PREFIX = "https://apps.apple.com/app/id"
private const val TEL_PREFIX = "tel:"

@OptIn(ExperimentalForeignApi::class)
internal class IosSystemNavigator(
    private val viewController: UIViewController,
    private val fileManager: FileManager,
    private val flavorInfoManager: ApplicationInfoManager,
) : SystemNavigator {

    private val application: UIApplication
        get() = UIApplication.sharedApplication

    private val mainScope = CoroutineScope(MyDispatchers.Main)

    override fun openSettings() {
        openUrl(APP_SETTINGS_URL) {}
    }

    override fun openAppSettings() {
        openUrl(APP_SETTINGS_URL) {}
    }

    override fun openMarket(packageNameOrAppId: String, onFailure: (Throwable) -> Unit) {
        openUrl(flavorInfoManager.source.baseUrl + packageNameOrAppId, onFailure) { success ->
            if (!success) {
                tryStoresInSequence(
                    AppStore.entries,
                    0,
                    packageNameOrAppId,
                    onFailure
                )
            }
        }
    }

    private fun tryStoresInSequence(
        stores: List<AppStore>,
        index: Int,
        packageNameOrAppId: String,
        onFailure: (Throwable) -> Unit,
    ) {
        if (index >= stores.size) {
            openUrl("$APP_STORE_WEB_FALLBACK_PREFIX$packageNameOrAppId", onFailure)
            return
        }
        openUrl(stores[index].baseUrl + packageNameOrAppId, onFailure) { success ->
            if (!success) {
                tryStoresInSequence(stores, index + 1, packageNameOrAppId, onFailure)
            }
        }
    }

    override fun openPhoneDialer(phoneNumber: String, onFailure: (Throwable) -> Unit) {
        val cleaned = phoneNumber.filter { it.isDigit() || it == '+' }
        val telUrlString = "$TEL_PREFIX$cleaned"
        val telUrl = NSURL.URLWithString(telUrlString)
        if (telUrl == null || !application.canOpenURL(telUrl)) {
            onFailure(IllegalStateException("Device has no phone dialer or cannot handle tel: URL"))
            return
        }
        openUrl(telUrlString, onFailure)
    }

    override fun openBrowser(url: String, onFailure: (Throwable) -> Unit) {
        val fullUrl = if (url.startsWith("http")) url else "https://$url"
        openUrl(fullUrl, onFailure)
    }

    override suspend fun share(text: String, title: String?, onFailure: (Throwable) -> Unit) {
        withContext(MyDispatchers.Main) {
            presentShare(activityItems = listOf(text), onFailure = onFailure)
        }
    }

    override suspend fun shareImage(image: Image, title: String?, onFailure: (Throwable) -> Unit) {
        if (!image.exists()) {
            onFailure(IllegalStateException("Image file does not exist: ${image.getPath()}"))
            return
        }
        withContext(MyDispatchers.Main) {
            val fileUrl = NSURL.fileURLWithPath(image.getPath())
            presentShare(activityItems = listOf(fileUrl), onFailure = onFailure)
        }
    }

    override suspend fun shareFile(byteArray: ByteArray, type: String, name: String, onFailure: (Throwable) -> Unit) {
        try {
            val file = fileManager.createFileFromBytes(name, byteArray)
            withContext(MyDispatchers.Main) {
                val fileUrl = NSURL.fileURLWithPath(file.getPath())
                presentShare(activityItems = listOf(fileUrl), onFailure = onFailure)
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    private fun openUrl(
        urlString: String,
        onFailure: (Throwable) -> Unit = {},
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        mainScope.launch {
            val url = NSURL.URLWithString(urlString) ?: run {
                onFailure(IllegalArgumentException("Invalid URL: $urlString"))
                onComplete?.invoke(false)
                return@launch
            }
            application.openURL(url, emptyMap<Any?, Any?>()) { success: Boolean ->
                if (!success) {
                    onFailure(RuntimeException("Failed to open URL: $urlString"))
                }
                onComplete?.invoke(success)
            }
        }
    }

    private fun presentShare(activityItems: List<Any>, onFailure: (Throwable) -> Unit) {
        try {
            val activityVC = UIActivityViewController(
                activityItems = activityItems,
                applicationActivities = null
            )
            viewController.presentViewController(activityVC, animated = true) {}
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
