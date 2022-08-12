package roadfriend.app.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import roadfriend.app.R
import roadfriend.app.widget.LoadingDialog

abstract class BindingBottomSheetDialogFragment<VB : ViewBinding> : BottomSheetDialogFragment() {
    private var _binding: VB? = null
    private var loadingDialog: LoadingDialog? = null
    var sheetState = BottomSheetBehavior.STATE_EXPANDED
        set(value) {
            field = value
            changeState(value)
        }

    protected val binding
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

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isFitToContents = true
            //expandedOffset = 300
            state = sheetState
            //  setPeekHeight(maxHeight.toInt())
            // halfExpandedRatio = 0.6f
        }

        onViewReady(savedInstanceState)
        onObserveState()
        onViewListener()
    }

    fun changeState(newState: Int) {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = newState
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    open fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(requireContext())
        }
        loadingDialog?.show()
    }

    open fun hideLoading() {
        loadingDialog?.dismiss()
    }

    open fun showErrorMessage(message: String) {
        (activity as BindingActivity<*>).showErrorMessage(message)
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog;
    }
}