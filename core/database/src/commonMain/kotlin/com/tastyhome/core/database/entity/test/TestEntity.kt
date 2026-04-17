package com.tastyhome.core.database.entity.test

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TestEntity")
data class TestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val message: String
)