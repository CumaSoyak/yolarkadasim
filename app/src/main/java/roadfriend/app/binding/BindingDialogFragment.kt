package com.biletdukkani.common.binding

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import roadfriend.app.binding.BindingActivity

abstract class BindingDialogFragment<VB : ViewBinding> : DialogFragment() {
    private var _binding: VB? = null

    val binding
        get() = _binding
            ?: throw IllegalStateException(
                "Cannot access view in after view destroyed " +
                        "and before view creation"
            )

    protected fun requireBinding(): VB = requireNotNull(_binding)

    protected var viewId: Int = -1

    abstract fun createBinding(): VB

    abstract fun onViewReady(bundle: Bundle?)

    open fun onViewListener() {}

    open fun onObserveState() {}

    override fun onStart() {
        super.onStart()
         val searchDialog: Dialog? = dialog
        if (searchDialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            searchDialog.window?.setLayout(width, height)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar)
    }
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = createBinding()
        return binding.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewId = binding.root.id
        onObserveState()
        onViewReady(savedInstanceState)
        onViewListener()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    open fun showLoading() {
        (activity as BindingActivity<*>).showLoading()
    }

    open fun hideLoading() {
        (activity as BindingActivity<*>).hideLoading()
    }

    open fun showErrorMessage(message: String) {
        (activity as BindingActivity<*>).showErrorMessage(message)
    }
}