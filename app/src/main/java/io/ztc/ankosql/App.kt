package io.ztc.ankosql

import android.app.Application
import com.shufeng.greendao.gen.DaoManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initGreenDao()
    }

    private fun initGreenDao() {
        val mManager = DaoManager.getInstance()
        mManager.init(this)
    }
}