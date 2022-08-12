package roadfriend.app

import android.content.Context
import org.koin.core.context.GlobalContext.startKoin
import roadfriend.app.base.BaseApplication
import roadfriend.app.di.appModule

/**
 * @Author: cumasoyak
 * @Date: 11.08.2022
 */
class YolArkadasiApp : BaseApplication() {
    companion object{
        lateinit var context:Context
    }

    override fun onCreate() {
        super.onCreate()
        context=this
        configureDi()


    }

    private fun configureDi() = startKoin {
          koin.loadModules(appModule)
    }
}