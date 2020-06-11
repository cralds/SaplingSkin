package com.sapling.saplingskin

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.widget.Toast
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.reflect.Array

/**
 *  create by cral
 *  create at 2020/6/10
 **/
class HotUpdateUtil {
    companion object {
        val instances by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HotUpdateUtil()
        }
    }
    var basePath = ""
    val dexName = "out.dex"

    fun startUpdate(context: Context){

        basePath = context.getDir("dexs",MODE_PRIVATE).absolutePath

        val savePath = basePath
        var fileRoot = File(savePath)
        if (!fileRoot.exists()){
            fileRoot.mkdir()
        }
        val listFiles = fileRoot.listFiles()

        if (listFiles == null || listFiles.size == 0){
            copyDex2SDCard(context,savePath)
            return
        }
        try{

            for (file in listFiles){
                var pathClassLoader = context.classLoader as PathClassLoader
                var classDex = Class.forName("dalvik.system.BaseDexClassLoader")
                var filedListFiled = classDex.getDeclaredField("pathList")
                filedListFiled.isAccessible = true
                var pathListValue = filedListFiled.get(pathClassLoader)
                //dexElements在pathList中
                var dexElementsFiled = pathListValue.javaClass.getDeclaredField("dexElements")
                dexElementsFiled.isAccessible = true
                //获取dexElements，热修复原理就是把增量包的dex和原包的dex合并 pathClassLoader
                var dexElementValue = dexElementsFiled.get(pathListValue)

                //获取增量包中的dex文件
                var dexClassLoader = DexClassLoader(file.absolutePath,
                    basePath,null,context.classLoader)

                var hotPathListValue = filedListFiled.get(dexClassLoader)
                var hotDexElementValue = dexElementsFiled.get(hotPathListValue)

                //合并数组
                val length = Array.getLength(dexElementValue)
                val lengthHot = Array.getLength(hotDexElementValue)

                val totalLen = length + lengthHot

                val componentType = dexElementValue.javaClass.componentType

                val sumDex = Array.newInstance(componentType, totalLen)

                for (i in 0..(totalLen-1)){
                    if (i < lengthHot){
                        Array.set(sumDex,i,Array.get(hotDexElementValue,i))
                    }else{
                        Array.set(sumDex,i,Array.get(dexElementValue,i-lengthHot))
                    }
                }

                dexElementsFiled.set(pathListValue,sumDex)

                Toast.makeText(context,"修复成功", Toast.LENGTH_LONG).show()
            }

        }catch (e : Exception){
        }


    }



    //通过该方式拷贝到SD卡中 在上面的方法中加载的dex长度为0，需要手动拷贝dex到SD卡
    fun copyDex2SDCard(context: Context,sdPath : String){
        val inputStream = context.assets.open(dexName)
        var file = File(basePath,dexName)
        if (!file.exists()){
            var os = FileOutputStream(file)
            var buffer = ByteArray(inputStream.available())
            var reader = inputStream.read(buffer)
            while (reader > 0){
                os.write(reader)
                reader = inputStream.read(buffer)
            }
            os.flush()
            os.close()
            inputStream.close()

        }
    }
}