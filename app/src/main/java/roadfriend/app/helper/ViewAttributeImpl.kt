
package roadfriend.app.helper

import android.content.res.TypedArray

interface ViewAttributeImpl {
    fun initStringAttribute(
        obtainedAttributes: TypedArray,
        styleableId: Int,
        action: ((String) -> Any)
    ) {
        getStringAttribute(obtainedAttributes, styleableId)?.let {
            action.invoke(it)
        }
    }

    fun initIntAttribute(
        obtainedAttributes: TypedArray,
        styleableId: Int,
        defValue: Int = 0,
        action: ((Int) -> Any)
    ) {
        val obtainedInt = getIntAttribute(obtainedAttributes, styleableId, defValue)
        action.invoke(obtainedInt)
    }

    fun initBooleanAttribute(
        obtainedAttributes: TypedArray,
        styleableId: Int,
        defValue: Boolean,
        action: ((Boolean) -> Any)
    ) {
        val obtainedBoolean = getBooleanAttribute(obtainedAttributes, styleableId, defValue)
        action.invoke(obtainedBoolean)
    }

    fun getStringAttribute(obtainedAttributes: TypedArray, styleableId: Int): String? {
        return obtainedAttributes.getString(styleableId)
    }

    fun getIntAttribute(obtainedAttributes: TypedArray, styleableId: Int, defValue: Int = 0): Int {
        return obtainedAttributes.getInt(styleableId, defValue)
    }

    fun getBooleanAttribute(
        obtainedAttributes: TypedArray,
        styleableId: Int,
        defValue: Boolean
    ): Boolean {
        return obtainedAttributes.getBoolean(styleableId, defValue)
    }
}