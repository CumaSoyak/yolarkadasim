/*
 * Copyright (c) 2021.
 *
 * Created by Yigit Can YILMAZ
 */

package roadfriend.app.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.viewbinding.ViewBinding
import roadfriend.app.helper.ViewAttributeImpl

/** This class is a main class for views that used in forms like EditTexts */
abstract class BaseCustomFormView<VB : ViewBinding> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle), ViewAttributeImpl {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    val binding: VB by lazy { createBinding() }

    abstract fun createBinding(): VB
    abstract fun showErrorMessage(isShow: Boolean, message: String? = null)
    abstract fun validateInput(): Boolean

}