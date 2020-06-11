package com.sapling.saplingskin

/**
 *  create by cral
 *  create at 2020/5/29
 *  每条属性封装的对象
 *  android:textColor="@color/title_textcolor"
 **/
class SkinItem {
    //属性名字 textcolor
    var name : String ? = null
    //属性的值的类型名字 @color
    var typeName : String ? = null
    //属性的值的名字  比如colorPrimary
    var attrName : String ? = null

    constructor(name: String?, typeName: String?, attrName: String?, resId: Int) {
        this.name = name
        this.typeName = typeName
        this.attrName = attrName
        this.resId = resId
    }

    var resId : Int = 0
}