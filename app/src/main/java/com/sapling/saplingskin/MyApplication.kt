package com.sapling.saplingskin

import android.app.Application
import android.content.Context

/**
 *  create by cral
 *  create at 2020/6/11
 **/
class MyApplication : Application(){
    override fun attachBaseContext(base: Context?) {
        //热修复
        HotUpdateUtil.instances.startUpdate(base!!)
        super.attachBaseContext(base)
    }
}