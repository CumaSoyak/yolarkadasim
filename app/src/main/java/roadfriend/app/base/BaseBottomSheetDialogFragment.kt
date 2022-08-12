package roadfriend.app.base

import androidx.viewbinding.ViewBinding
import roadfriend.app.binding.BindingBottomSheetDialogFragment
import roadfriend.app.extension.errorAlerter
import roadfriend.app.extension.observeLiveData
import roadfriend.app.extension.successAlerter

abstract class BaseBottomSheetDialogFragment<VB : ViewBinding, VM : BaseViewModel> :
    BindingBottomSheetDialogFragment<VB>() {
    abstract val viewModel: VM

    override fun onObserveState() {
        super.onObserveState()
        observeLoadingState()
        observeErrorMessage()
    }

    private fun observeLoadingState() {
        observeLiveData(viewModel.isLoading) { state ->
            state?.let {
                if (it) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }
    }

    private fun observeErrorMessage() {
        observeLiveData(viewModel.errorMessageAlerter) { message ->
            requireDialog().errorAlerter(message)
            println("Alerter! -> $message")
        }
        observeLiveData(viewModel.succesMessageAlerter) { message ->
            requireDialog().successAlerter(message)
        }
    }

}