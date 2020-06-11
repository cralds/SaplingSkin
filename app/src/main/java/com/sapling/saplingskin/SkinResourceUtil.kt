package com.sapling.saplingskin

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 *  create by cral
 *  create at 2020/5/28
 **/
class SkinResourceUtil{

    companion object {
        val instances : SkinResourceUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SkinResourceUtil()
        }
    }
    private lateinit var context : Context

    private lateinit var packageName : String
    private lateinit var skinRes : Resources
    fun setContext(context: Context){
        this.context = context
    }

    /**
     * 加载皮肤包
     */
    fun loadSkin(path : String){
        var assetManager = AssetManager::class.java.newInstance()
        var method = assetManager::class.java.getMethod("addAssetPath",String::class.java)
        method.invoke(assetManager,path)
        val resources = context.resources
        skinRes = Resources(assetManager,resources.displayMetrics,resources.configuration)

        var packageInfo = context.packageManager.getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES)
        packageInfo?.let {
            packageName = context.packageManager.getPackageArchiveInfo(path,PackageManager.GET_ACTIVITIES).packageName
        }
    }

    fun getSkinResId(resId : Int) : Int{
        var result = resId
        if (skinRes != null){

            //获取属性的名字 比如colorPrimary
            val name = context.resources.getResourceEntryName(resId)
            //获取属性的类型  比如@color
            var typeName = context.resources.getResourceTypeName(resId)

            //判断皮肤包里面是否有同名的资源，有就返回皮肤包里面的资源
            var skinColor = skinRes.getIdentifier(name,typeName,packageName)
            if (skinColor != 0 ){
                result = skinColor
            }
        }
        return result
    }

    fun getResColor(resId : Int) : Int{
        var skinResId = getSkinResId(resId)
        if (resId != skinResId){
            return skinRes.getColor(skinResId)
        }
        return context.resources.getColor(resId)
    }


    fun getColor(resId : Int) : Int{
        var result = resId
        if (skinRes != null){

            //获取属性的名字 比如colorPrimary
            val name = context.resources.getResourceEntryName(resId)
            //获取属性的类型  比如@color
            var typeName = context.resources.getResourceTypeName(resId)

            //判断皮肤包里面是否有同名的资源，有就返回皮肤包里面的资源
            var skinColor = skinRes.getIdentifier(name,typeName,packageName)
            if (skinColor != 0 ){
                result = skinColor
            }
        }
        return result
    }

    fun getDrawable(resId: Int) : Drawable{
        val skinResId = getSkinResId(resId)
        var drawable = skinRes.getDrawable(skinResId)
        return drawable
    }
}