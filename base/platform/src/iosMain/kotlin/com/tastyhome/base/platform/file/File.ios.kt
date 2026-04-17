package com.tastyhome.base.platform.file

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
actual open class File(val url: NSURL) {
    
    private val fileManager = NSFileManager.defaultManager
    
    actual fun getName(): String = url.lastPathComponent ?: ""
    
    actual fun getPath(): String = url.path ?: ""
    
    actual fun getExtension(): String = url.pathExtension ?: ""
    
    actual fun exists(): Boolean = fileManager.fileExistsAtPath(getPath())
    
    actual fun size(): Long {
        if (!exists()) return 0L
        
        val attributes = fileManager.attributesOfItemAtPath(getPath(), error = null)
        return (attributes?.get("NSFileSize") as? Long) ?: 0L
    }
    
    actual fun delete(): Boolean {
        return if (exists()) fileManager.removeItemAtURL(url, error = null) else false
    }
    
    override fun toString(): String = "File(path=${getPath()})"
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as File
        return getPath() == other.getPath()
    }
    
    override fun hashCode(): Int = getPath().hashCode()
}
