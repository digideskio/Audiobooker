package com.marche.audiobookier

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.marche.audiobookier.data.local.LocalRepository
import com.marche.audiobookier.data.model.MyObjectBox
import com.singhajit.sherlock.core.Sherlock
import com.squareup.leakcanary.LeakCanary
import com.tspoon.traceur.Traceur
import com.marche.audiobookier.injection.component.AppComponent
import com.marche.audiobookier.injection.component.DaggerAppComponent
import com.marche.audiobookier.injection.module.AppModule
import com.marche.audiobookier.injection.module.NetworkModule
import io.objectbox.BoxStore
import timber.log.Timber

class AudiobookerApplication : MultiDexApplication() {

    private var appComponent: AppComponent? = null

    lateinit var localRepository: LocalRepository

    companion object {
        operator fun get(context: Context): AudiobookerApplication {
            return context.applicationContext as AudiobookerApplication
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
            LeakCanary.install(this)
            Sherlock.init(this)
            Traceur.enableLogging()
        }

        localRepository = LocalRepository(getBoxStore())
    }

    private fun getBoxStore(): BoxStore{
        return MyObjectBox.builder().androidContext(applicationContext).build()
    }

    // Needed to replace the component with a test specific one
    var component: AppComponent
        get() {
            if (appComponent == null) {
                appComponent = DaggerAppComponent.builder()
                        .appModule(AppModule(this))
                        .networkModule(NetworkModule(this))
                        .build()
            }
            return appComponent as AppComponent
        }
        set(appComponent) {
            this.appComponent = appComponent
        }

}