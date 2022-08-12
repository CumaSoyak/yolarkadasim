package roadfriend.app.base

import androidx.viewbinding.ViewBinding
import roadfriend.app.binding.BindingActivity
import roadfriend.app.extension.observeLiveData
import roadfriend.app.extension.toast
import roadfriend.app.widget.LoadingDialog


abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : BindingActivity<VB>() {

    private var loadingDialog: LoadingDialog? = null
    abstract val viewModel: VM
    var customBackAction: (() -> Unit)? = null

    override fun onBackPressed() {
        customBackAction?.let {
            it.invoke()
            return
        }
        super.onBackPressed()
    }

    override fun showLoading(isTransfer: Boolean) {
        super.showLoading(isTransfer)
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this, isTransfer)
        }
        loadingDialog?.show()
    }

    override fun hideLoading() {
        super.hideLoading()
        loadingDialog?.dismiss()
    }

    override fun showErrorMessage(message: String) {
        super.showErrorMessage(message)
        toast(message)
    }

    override fun onObserveState() {
        super.onObserveState()
        observeLoadingState()
        observeErrorMessage()
    }

    private fun observeLoadingState() {
        observeLiveData(viewModel.isLoading) { result ->
            if (result == true) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    private fun observeErrorMessage() {
        observeLiveData(viewModel.errorMessage) { message ->
            message?.let { showErrorMessage(it) }
        }
    }

}