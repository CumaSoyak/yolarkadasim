package roadfriend.app.base

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import roadfriend.app.BuildConfig
import roadfriend.app.helper.ActivityLifecycleCallback
import java.util.*

open class BaseApplication : Application(), LifecycleObserver {
    companion object {
        lateinit var instance: BaseApplication
            private set
    }

    open fun timberLogsEnable(): Boolean = true

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(ActivityLifecycleCallback())
        instance = this


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        //App in background
     }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        //App in foreground
     }


}