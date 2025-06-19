package org.itb.nominas

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.itb.nominas.core.di.initKoin
import org.itb.nominas.core.platform.initSettingsContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initSettingsContext(this)
        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
        }

        Napier.base(DebugAntilog())
    }
}