package com.mooncode.lorenzoskitchen

import android.app.Application
import com.google.android.material.color.DynamicColors


import io.realm.Realm
import io.realm.RealmConfiguration

class App: Application()    {
    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        super.onCreate()

        Realm.init(this)
        val config: RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}