package roadfriend.app.base

import androidx.viewbinding.ViewBinding
import com.biletdukkani.common.binding.BindingDialogFragment
import roadfriend.app.extension.observeLiveData


abstract class BaseDialogFragment<VB : ViewBinding, VM : BaseViewModel> :
    BindingDialogFragment<VB>() {
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
        observeLiveData(viewModel.errorMessage) { message ->
            message?.let {
              //  DialogUtils.DialogModel(context = requireContext(), title = it).showErrorDialog()
            }
        }
        observeLiveData(viewModel.succesMessage) { message ->
            message?.let {
               // DialogUtils.DialogModel(context = requireContext(), title = it).showSuccesDialog {}
            }
        }
    }



}