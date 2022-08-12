package roadfriend.app.base

import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.biletdukkani.common.binding.BindingFragment
import roadfriend.app.extension.errorAlerter
import roadfriend.app.extension.observeLiveData
import roadfriend.app.extension.successAlerter



abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : BindingFragment<VB>() {
    abstract val viewModel: VM
    override fun onObserveState() {
        super.onObserveState()
        observeLoadingState()
        observeDialogMessage()
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

    private fun observeDialogMessage() {
        observeLiveData(viewModel.errorMessage) { message ->
            message?.let {
              //  DialogUtils.DialogModel(context = requireContext(), title = it).showErrorDialog()
            }
        }
        observeLiveData(viewModel.succesMessage) { message ->
            message?.let {
              //  DialogUtils.DialogModel(context = requireContext(), title = it).showSuccesDialog {}
            }
        }
        observeLiveData(viewModel.errorMessageAlerter) { message ->
            requireActivity().errorAlerter(message)
        }
        observeLiveData(viewModel.succesMessageAlerter) { message ->
            requireActivity().successAlerter(message)
        }
    }


    override fun onDestroy() {
        viewModelStore.clear()
        super.onDestroy()
    }

    protected fun showDialog(
        @StringRes titleRes: Int,
        @StringRes message: Int,
        positiveButtonText: String?,
        negativeButtonText: String?,
        isCancelable: Boolean,
        onOkClick: (DialogInterface) -> Unit,
        onCancel: (DialogInterface) -> Unit = {}
    ): AlertDialog = AlertDialog.Builder(requireContext())
        .setTitle(titleRes)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setPositiveButton(positiveButtonText) { dialog, _ -> onOkClick(dialog) }
        .setNegativeButton(negativeButtonText) { dialog, _ -> onCancel(dialog) }
        .show()





}