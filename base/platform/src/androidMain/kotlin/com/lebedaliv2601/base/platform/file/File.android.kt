package com.lebedaliv2601.base.platform.file

import java.io.File

actual open class File(val file: File) {

    actual fun getName(): String = file.name
    
    actual fun getPath(): String = file.absolutePath
    
    actual fun getExtension(): String = file.extension
    
    actual fun exists(): Boolean = file.exists()
    
    actual fun size(): Long = if (file.exists()) file.length() else 0L
    
    actual fun delete(): Boolean = if (file.exists()) file.delete() else false
    
    override fun toString(): String = "File(path=${getPath()})"
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as com.lebedaliv2601.base.platform.file.File
        return file.absolutePath == other.file.absolutePath
    }
    
    override fun hashCode(): Int = file.absolutePath.hashCode()
}
