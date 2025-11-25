package com.autosync.main

import android.app.Application
import com.autosync.main.data.local.UserDatabase

class MainApplication : Application() {
    val database: UserDatabase by lazy { UserDatabase.getDatabase(this) }
}
