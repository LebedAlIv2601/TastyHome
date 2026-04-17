@file:Suppress("detekt:TopLevelPropertyNaming")

package com.tastyhome.base.platform.permissions

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject
import kotlin.coroutines.resume

private const val UNAuthorizationOptionBadge = 1u
private const val UNAuthorizationOptionSound = 2u
private const val UNAuthorizationOptionAlert = 4u
private val UNAuthorizationOptionsDefault =
    UNAuthorizationOptionBadge or UNAuthorizationOptionSound or UNAuthorizationOptionAlert

private const val AVAuthorizationStatusAuthorized = 3
private const val AVAuthorizationStatusDenied = 2
private const val AVAuthorizationStatusRestricted = 1

private const val UNAuthorizationStatusAuthorized = 2
private const val UNAuthorizationStatusDenied = 1

internal class IosPermissionManager : PermissionManager {

    private val notificationCenter get() = UNUserNotificationCenter.currentNotificationCenter()
    private val locationManager = CLLocationManager()

    override suspend fun checkPermission(permission: Permission): PermissionStatus = when (permission) {
        Permission.NOTIFICATION -> checkNotification()
        Permission.CAMERA -> avStatusToPermission(
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo).toInt()
        )

        Permission.MICROPHONE -> avStatusToPermission(
            AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio).toInt()
        )

        Permission.LOCATION -> locationStatusToPermission(locationManager.authorizationStatus)
        Permission.MEDIA, Permission.STORAGE -> photosStatusToPermission(PHPhotoLibrary.authorizationStatus().toInt())
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus = when (permission) {
        Permission.NOTIFICATION -> requestNotification()
        Permission.CAMERA -> requestAvMediaType(AVMediaTypeVideo)
        Permission.MICROPHONE -> requestAvMediaType(AVMediaTypeAudio)
        Permission.LOCATION -> requestLocation()
        Permission.MEDIA, Permission.STORAGE -> requestPhotos()
    }

    private fun avStatusToPermission(status: Int): PermissionStatus = when (status) {
        AVAuthorizationStatusAuthorized -> PermissionStatus.GRANTED
        AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> PermissionStatus.DENIED
        else -> PermissionStatus.NOT_DETERMINED
    }

    private fun locationStatusToPermission(status: Int): PermissionStatus = when (status) {
        kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> PermissionStatus.GRANTED
        kCLAuthorizationStatusDenied -> PermissionStatus.DENIED
        kCLAuthorizationStatusRestricted -> PermissionStatus.DENIED_FOREVER
        else -> PermissionStatus.NOT_DETERMINED
    }

    private fun photosStatusToPermission(status: Int): PermissionStatus = when (status) {
        PHAuthorizationStatusAuthorized.toInt() -> PermissionStatus.GRANTED
        PHAuthorizationStatusDenied.toInt() -> PermissionStatus.DENIED
        PHAuthorizationStatusRestricted.toInt() -> PermissionStatus.DENIED_FOREVER
        else -> PermissionStatus.NOT_DETERMINED
    }

    private suspend fun checkNotification(): PermissionStatus = suspendCancellableCoroutine { cont ->
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            val raw = settings?.authorizationStatus?.toInt() ?: 0
            val status = when (raw) {
                UNAuthorizationStatusAuthorized -> PermissionStatus.GRANTED
                UNAuthorizationStatusDenied -> PermissionStatus.DENIED
                else -> PermissionStatus.NOT_DETERMINED
            }
            cont.resume(status)
        }
    }

    private suspend fun requestNotification(): PermissionStatus = suspendCancellableCoroutine { cont ->
        notificationCenter.requestAuthorizationWithOptions(UNAuthorizationOptionsDefault.toULong()) { granted, _ ->
            cont.resume(if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED)
        }
    }

    private suspend fun requestAvMediaType(mediaType: String?): PermissionStatus = suspendCancellableCoroutine { cont ->
        AVCaptureDevice.requestAccessForMediaType(mediaType) { granted ->
            cont.resume(if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED)
        }
    }

    private suspend fun requestLocation(): PermissionStatus {
        if (locationStatusToPermission(locationManager.authorizationStatus) != PermissionStatus.NOT_DETERMINED)
            return locationStatusToPermission(locationManager.authorizationStatus)
        return suspendCancellableCoroutine { cont ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
                    if (didChangeAuthorizationStatus != kCLAuthorizationStatusNotDetermined) {
                        locationManager.setDelegate(null)
                        cont.resume(locationStatusToPermission(didChangeAuthorizationStatus))
                    }
                }
            }
            locationManager.setDelegate(delegate)
            locationManager.requestWhenInUseAuthorization()
        }
    }

    private suspend fun requestPhotos(): PermissionStatus = suspendCancellableCoroutine { cont ->
        PHPhotoLibrary.requestAuthorization { status ->
            cont.resume(photosStatusToPermission(status.toInt()))
        }
    }
}
