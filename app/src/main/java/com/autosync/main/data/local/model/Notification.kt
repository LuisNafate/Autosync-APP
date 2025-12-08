package com.autosync.main.data.local.model

import java.util.Date

data class Notification(
    val id: String = "",
    val userId: String = "",
    val message: String = "",
    val type: String = "", // "VEHICLE", "SERVICE"
    val relatedId: String = "",
    val date: Date = Date(),
    val read: Boolean = false
)
