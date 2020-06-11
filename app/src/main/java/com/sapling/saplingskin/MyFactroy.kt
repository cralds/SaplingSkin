package com.sapling.saplingskin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 *  create by cral
 *  create at 2020/5/28
 **/
class MyFactroy : LayoutInflater.Factory2 {
    val packageNames = arrayOf("android.view.","android.widget.")

    val viewList = mutableListOf<SkinView>()
    lateinit var context : Context

    override fun onCreateView(parent: View?, name: String?, context: Context?, attrs: AttributeSet?): View {
        var view : View? = null
        this.context = context!!
        if (name!!.contains(".")){//自定义控件
            view = onCreateView(name,context,attrs)
        }else{
            for (pkgName in packageNames){//系统控件
                view = onCreateView(pkgName+name,context,attrs)
                if (view != null){
                    parseView(view,context,attrs)
                    return view
                }
            }
        }

        return view!!
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        var view : View? = null
        try {
            val cls = context!!.getClassLoader().loadClass(name)
            var constructor = cls.getConstructor(Context::class.java,AttributeSet::class.java)
            view = constructor.newInstance(context,attrs) as View
        }catch (e : Exception){

        }
        return view
    }

    /**
     * 封装view和该view对应可能被重新设值换肤的属性
     */
    protected fun parseView(view: View,context: Context?,attrs: AttributeSet?) {
        var attrList = mutableListOf<SkinItem>()
        var count = attrs!!.attributeCount -1
        for ( i in  0..count){
            //获取属性名称
            var attrName = attrs.getAttributeName(i)
            if (attrName.contains("background") || attrName.contains("textColor")
                ||attrName.contains("src")) {
                //获取到资源id
                var value = attrs.getAttributeValue(i)

                if (value.startsWith("#")){
                    continue
                }
                var resId = value.substring(1).toInt()

                var name = context?.resources?.getResourceEntryName(resId)

                var typeName = context?.resources?.getResourceTypeName(resId)

                var skinItem = SkinItem(attrName, typeName, name, resId)


                attrList.add(skinItem)
            }
        }
        if (attrList.size > 0){
            var skinView = SkinView()
            skinView.list = attrList
            skinView.view = view;

            viewList.add(skinView)
        }
    }

    fun apply(){
        for (skinView in viewList){
            var view = skinView.view

            for (skinItem in skinView.list!!){

                when(skinItem.name){
                    "textColor" -> {
                        (view as TextView).setTextColor(SkinResourceUtil.instances.getResColor(skinItem.resId))
                    }
                    "background" -> {
                        if (skinItem.typeName.equals("color")){
                            view!!.setBackgroundColor(SkinResourceUtil.instances.getResColor(skinItem.resId))
                        }else{//drawable
                            view!!.setBackgroundResource(SkinResourceUtil.instances.getColor(skinItem.resId))
                        }

                    }
                    "src" -> {
                        (view as ImageView).setImageDrawable(SkinResourceUtil.instances.getDrawable(skinItem.resId))
                    }
                }
            }
        }
    }
}