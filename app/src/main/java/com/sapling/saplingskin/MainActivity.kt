package com.sapling.saplingskin

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.view.LayoutInflaterCompat
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var myFactroy: MyFactroy
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var layoutInflater = LayoutInflater.from(this)
        setLayoutInflaterFactory(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
        }
        myFactroy = MyFactroy()
        LayoutInflaterCompat.setFactory2(layoutInflater,myFactroy)
        setContentView(R.layout.activity_main)
        val path = Environment.getExternalStorageDirectory().absolutePath + "/blackskin.apk"
        SkinResourceUtil.instances.setContext(this)
        tvChange.setOnClickListener {
            SkinResourceUtil.instances.loadSkin(path)
            myFactroy.apply()
        }
        tvRun.setOnClickListener {
            val len = TextHotUpdate().showStrLen(null)
            Toast.makeText(this@MainActivity,""+len,Toast.LENGTH_LONG).show()
        }

    }

    fun setLayoutInflaterFactory(original: LayoutInflater) {
        try {
            val mFactorySet = LayoutInflater::class.java.getDeclaredField("mFactorySet")
            mFactorySet.isAccessible = true
            mFactorySet.set(original, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
