package roadfriend.app.ui.profile.password

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.biletdukkani.b2c.customViews.CustomDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import roadfriend.app.R
import roadfriend.app.base.BaseFragment
import roadfriend.app.databinding.DialogForgetPasswordBinding
import roadfriend.app.databinding.PasswordFragmentBinding
import roadfriend.app.extension.*
import roadfriend.app.helper.FirebaseAuthHelper
import roadfriend.app.helper.Helpers
import roadfriend.app.utils.PrefUtils

class PasswordFragment : BaseFragment<PasswordFragmentBinding, PasswordViewModel>() {

    private var isLogin: Boolean? = null
    private var isResetPassword: Boolean? = null
    private var email: String? = null
    private val firebaseAuthHelper: FirebaseAuthHelper by inject()

    override val viewModel: PasswordViewModel by viewModel()

    override fun onViewReady(bundle: Bundle?) {
        checkArguments()
        initScreen()
    }

    private fun checkArguments() {
        arguments?.let {
            if (it.containsKey("isLogin")) {
                isLogin = it.getBoolean("isLogin")
            }
            if (it.containsKey("email")) {
                email = it.getString("email")
            }
            if (it.containsKey("isResetPassword")) {
                isResetPassword = it.getBoolean("isResetPassword")
            }
        }

        Helpers().handleDeepLink()?.let {
             Helpers().removeDeepLink()
        }
    }

    private fun initScreen() {
        binding.etPassword.disableEmojis()
        binding.etPasswordAgain.disableEmojis()
        when {
            isLogin == true -> {
                initLoginScreen()
            }
            isLogin == false -> {
                initRegisterScreen()
            }
            isResetPassword == true -> {
                initResetPasswordScreen()
            }
            else -> {
                findNavController().popBackStack()
                return
            }
        }
        initShowHidePassword()
    }

    private fun initShowHidePassword() {
        binding.ivShowHidePassword.setOnClickListener {
            binding.etPassword.showHidePassword { isShown ->
                if (isShown) {
                    binding.ivShowHidePassword.setImageResource(R.drawable.ic_android)
                } else {
                    binding.ivShowHidePassword.setImageResource(R.drawable.ic_ios)
                }
            }
            binding.etPasswordAgain.showHidePassword {}
        }
    }

    private fun initLoginScreen() {
        binding.llPasswordAgain.isVisible = false
        binding.flLoginUtils.isVisible = true
        binding.btnContinue.text = get("biletdukkani_general_login_button_signin")
        binding.tvHeader.text = get("biletdukkani_login_label_enter_password_title")
        binding.tvInfo.text = get("biletdukkani_password_label_please_enter_password_info").replace(
            "#{mail}",
            email.toString()
        )
        binding.tvForgetMyPassword.setOnClickListener {
            firebaseAuthHelper.sendPasswordResetEmail(email!!) {
                showForgetPasswordDialog()
            }
        }
        binding.btnContinue.setOnClickListener {
            if (binding.etPassword.text.toString().trim().isNotNullOrEmpty())
                firebaseAuthHelper.signInWithEmailAndPassword(
                    email!!,
                    binding.etPassword.text.toString()
                ) { isSuccessful ->
                    if (isSuccessful) {
                        println("SigninSuccessful")
                        requireActivity().finish()
                        PrefUtils.isLogin = true
                    } else {
                        toast(get("biletdukkani_pnrsearch_dialog_label_pnr_not_found_description"))
                    }
                }
            else {
                toast(get("biletdukkani_general_error_incorrect_information"))
            }
        }
    }

    private fun initRegisterScreen() {
        binding.llPasswordAgain.isVisible = true
        binding.flLoginUtils.isVisible = false
        binding.btnContinue.text = get("biletdukkani_login_label_create_account")
        binding.tvHeader.text = get("biletdukkani_login_label_create_new_account")
        binding.tvInfo.text =
            get("biletdukkani_password_label_please_create_password_info").replace(
                "#{mail}",
                email.toString()
            )
        binding.btnContinue.setOnClickListener {
            createAccount()
        }
    }

    private fun initResetPasswordScreen() {
        binding.llPasswordAgain.isVisible = true
        binding.flLoginUtils.isVisible = false
        binding.btnContinue.text = get("biletdukkani_password_label_reset_password_title")
        binding.tvHeader.text = get("biletdukkani_password_label_reset_password_desc")
        binding.tvInfo.text = get("biletdukkani_password_label_enter_new_password")
        binding.btnContinue.setOnClickListener {
            resetPassword()
        }
    }

    private fun createAccount() {
        if (!isPasswordFieldsValid()) {
            return
        }
        showLoading()
        firebaseAuthHelper.createUserWithEmailAndPassword(
            email!!,
            binding.etPassword.text.toString()
        ) { isSuccessful ->
            hideLoading()
            if (isSuccessful) {
                println("RegisterSuccessful")
                PrefUtils.isLogin = true
                firebaseAuthHelper.sendVerificationEmail { requireActivity().finish() }
            } else {
                //   CrashlyticsLogger.log("${PasswordFragment::class.java.simpleName} createAccount $email")
                toast("RegisterFailed")
            }
        }
    }

    private fun resetPassword() {
        if (!isPasswordFieldsValid()) {
            return
        }
        val password = binding.etPassword.text.toString()

        val code = " "//deepLinkData.queries["oobCode"] ?: ""
        showLoading()
        firebaseAuthHelper.resetPassword(code, password) { isSuccessful ->
            hideLoading()
            if (isSuccessful) {
                println("passwordResetSuccess")
                requireActivity().finish()
            } else {
               // CrashlyticsLogger.log("${PasswordFragment::class.java.simpleName} resetPassword $code")
                toast("passwordResetFailed")
            }
        }


    }

    private fun isPasswordFieldsValid(): Boolean {
        val password = binding.etPassword.text.toString()
        val passwordAgain = binding.etPasswordAgain.text.toString()
        if (password != passwordAgain) {
            toast(get("biletdukkani_profil_error_password_matching"))
            return false
        } else if (!Helpers().isPasswordValid(password)) {
            toast(get("biletdukkani_login_alert_password_include_regex"))
            return false
        }
        return true
    }

    private fun showForgetPasswordDialog() {
        val dialogBinding = DialogForgetPasswordBinding.inflate(layoutInflater)
        dialogBinding.tvTitle.text = get("biletdukkani_password_label_has_been_sent")
        dialogBinding.tvInfo.text =
            get("biletdukkani_password_label_reset_mail_sent").replace("#{mail}", email.toString())
        val dialog = CustomDialog(requireContext(), dialogBinding)
        dialogBinding.btnContinue.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDetach() {
        super.onDetach()
      //  (requireActivity() as LoginActivity).appbarHelper.isShowAppbar(false)
    }

    override fun createBinding(): PasswordFragmentBinding {
        return PasswordFragmentBinding.inflate(layoutInflater)
    }

}