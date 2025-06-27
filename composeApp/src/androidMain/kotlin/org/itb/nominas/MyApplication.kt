package org.itb.nominas

import android.app.Application
import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.itb.nominas.core.di.initKoin
import org.itb.nominas.core.platform.initSettingsContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level


class MyApplication : Application() {
    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        initSettingsContext(this)
        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MyApplication)
        }

        Napier.base(DebugAntilog())
    }
}