package com.biletdukkani.common.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import roadfriend.app.binding.BindingActivity

abstract class BindingFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null

    val binding
        get() = _binding
            ?: throw IllegalStateException(
                "Cannot access view in after view destroyed " +
                        "and before view creation"
            )


    abstract fun createBinding(): VB

    abstract fun onViewReady(bundle: Bundle?)

    open fun onViewListener() = Unit

    open fun onStringsSetCompleted() = Unit

    open fun onObserveState() = Unit

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
        onObserveState()
        onViewReady(savedInstanceState)
        onViewListener()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(100)
            onStringsSetCompleted()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    open fun showLoading(isTransfer: Boolean = false) {
        activity?.let {
            (activity as BindingActivity<*>).showLoading(isTransfer)
        }
    }

    open fun hideLoading() {
        activity?.let {
            (activity as BindingActivity<*>).hideLoading()
        }
    }

    open fun showErrorMessage(message: String) {
        activity?.let {
            (activity as BindingActivity<*>).showErrorMessage(message)
        }

    }



}