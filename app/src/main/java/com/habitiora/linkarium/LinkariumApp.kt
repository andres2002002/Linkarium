package com.habitiora.linkarium

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class LinkariumApp: Application() {
    //@Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
            Timber.i("onCreate: Timber inicializado")
        } else {
            plant(CrashReportingTree())
            Timber.i("onCreate: Timber inicializado en modo producción")
        }
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        //val crashlytics = FirebaseCrashlytics.getInstance()
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
//            if (priority == Log.VERBOSE || priority == Log.DEBUG) return
//            val messageFirebase = "$tag: $message, ${t?.message}"
//            crashlytics.log(messageFirebase)
//            if (t != null) {
//                crashlytics.recordException(t)
//            }
        }
    }
}