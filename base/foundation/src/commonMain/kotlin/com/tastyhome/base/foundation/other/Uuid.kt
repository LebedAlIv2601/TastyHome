package com.tastyhome.base.foundation.other

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun randomUuid(): String = Uuid.random().toString()