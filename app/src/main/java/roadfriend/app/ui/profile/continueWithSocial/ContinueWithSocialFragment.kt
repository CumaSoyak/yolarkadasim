package roadfriend.app.ui.profile.continueWithSocial

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import roadfriend.app.R
import roadfriend.app.base.BaseFragment
import roadfriend.app.databinding.ContinueWithSocialFragmentBinding
import roadfriend.app.enums.AuthProviders
import roadfriend.app.extension.disableEmojis
import roadfriend.app.extension.get
import roadfriend.app.extension.showHidePassword
import roadfriend.app.extension.toast
import roadfriend.app.helper.FirebaseAuthHelper
import roadfriend.app.utils.PrefUtils

class ContinueWithSocialFragment :
    BaseFragment<ContinueWithSocialFragmentBinding, ContinueWithSocialViewModel>() {

    private var email: String = ""
    private var currentProvider: String = ""
    private var newProvider: String = ""
    private var providerConnectAction: (() -> Unit)? = null
    private val firebaseAuthHelper: FirebaseAuthHelper by inject()
    private val registeredActivityForGoogleSignInResult = onGoogleSignInActivityResult()
    private lateinit var facebookCallbackManager: CallbackManager

    override val viewModel: ContinueWithSocialViewModel by viewModel()

    override fun onViewReady(bundle: Bundle?) {
        arguments?.let { args ->
            email = args.getString("email") ?: ""
            currentProvider = args.getString("currentProvider") ?: ""
            newProvider = args.getString("newProvider") ?: ""
           // CrashlyticsLogger.log("$email - $currentProvider - $newProvider")
        }
        initScreen()
    }

    private fun initScreen() {
        binding.etPassword.disableEmojis()
        initShowHidePassword()
        initFacebookLogin()
        var currentProviderName = ""
        var newProviderName = ""
        when {
            /*AuthProviders.Email.value -> {
                providerName = "E-posta"
                binding.tvButton.text = getString(R.string.continue_with_email)
                binding.ivButton.setImageDrawable(null)
                providerConnectAction = {
                    val bundle = bundleOf("isLogin" to true, "email" to email)
                    findNavController().navigate(R.id.action_continueWithSocialFragment_to_passwordFragment, bundle)
                }
            }*/
            //Google hesabı varsa şifre oluşturulacaksa
            currentProvider == AuthProviders.Google.value && newProvider == AuthProviders.Email.value -> {
                currentProviderName = "Google"
                newProviderName = "Email"
                binding.tvTitle.text = get("biletdukkani_login_label_continue_with_account").replace("#{provider}",currentProviderName)
                binding.tvInfo.text= get("biletdukkani_login_label_your_mail_is_connected_to").replace("#{mail}",email).replace("#{provider}",currentProviderName)
                binding.ivButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_ios
                    )
                )
                binding.tvButton.text = get("biletdukkani_login_button_continur_with_google")
                providerConnectAction = { signInWithGoogle() }
            }
            //Facebook hesabı varsa şifre oluşturulacaksa
            currentProvider == AuthProviders.Facebook.value && newProvider == AuthProviders.Email.value -> {
                currentProviderName = "Facebook"
                newProviderName = "E-posta"
                binding.tvTitle.text = get("biletdukkani_login_label_continue_with_account").replace("#{provider}",currentProviderName)
                binding.tvInfo.text= get("biletdukkani_login_label_your_mail_is_connected_to").replace("#{mail}",email).replace("#{provider}",currentProviderName)
                binding.ivButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_android
                    )
                )
                binding.tvButton.text = get("biletdukkani_login_button_continue_with_facebook")
                providerConnectAction = {
                    firebaseAuthHelper.facebookLoginManager.logInWithReadPermissions(
                        this,
                        listOf("user_photos", "email", "user_birthday", "public_profile")
                    )
                }
            }
            //Email hesabı varsa, Google hesabı bağlamak istiyorsa
            currentProvider == AuthProviders.Email.value && newProvider == AuthProviders.Google.value -> {
                currentProviderName = "E-posta"
                newProviderName = "Google"
                binding.tvTitle.text = get("biletdukkani_login_label_connect_with_account").replace("#{provider}",newProviderName)
                binding.tvInfo.text= get("biletdukkani_login_label_link_your_social_account_description_password").replace("#{mail}",email).replace("#{currentprovider}",currentProviderName).replace("#{newprovider}",newProviderName)
                binding.ivButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_ios
                    )
                )
                binding.tvButton.text = get("biletdukkani_login_label_connect_with_account").replace("#{provider}",newProviderName)
                binding.flPassword.isVisible = true
                providerConnectAction = { verifySignInAndLinkAccounts(true) }
            }
            //Email hesabı varsa, Facebook hesabı bağlamak istiyorsa
            currentProvider == AuthProviders.Email.value && newProvider == AuthProviders.Facebook.value -> {
                currentProviderName = "E-posta"
                newProviderName = "Facebook"
                binding.tvTitle.text = get("biletdukkani_login_label_connect_with_account").replace("#{provider}",newProviderName)
                binding.tvInfo.text=get("biletdukkani_login_label_link_your_social_account_description_password").replace("#{mail}",email).replace("#{currentprovider}",currentProviderName).replace("#{newprovider}",newProviderName)
                binding.ivButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_android
                    )
                )
                binding.tvButton.text = get("biletdukkani_login_label_connect_with_account").replace("#{provider}",newProviderName)
                binding.flPassword.isVisible = true
                providerConnectAction = { verifySignInAndLinkAccounts(false) }
            }
            //Google hesabı varsa, Facebook bağlamak istiyorsa
            currentProvider == AuthProviders.Google.value && newProvider == AuthProviders.Facebook.value -> {
                //TODO: Google hesabı seçtir, doğru mail adresiyse bağla, değilse uyarı ver
            }
            //Facebook hesabı varsa ve Google bağlamak istiyorsa
            currentProvider == AuthProviders.Facebook.value && newProvider == AuthProviders.Google.value -> {
                //TODO: Facebook login yap, doğru mail adresiyse bağla, değilse uyarı ver
            }
        }
        binding.signIn.setOnClickListener {
            providerConnectAction?.invoke()
        }
    }

    private fun initShowHidePassword() {
        binding.ivShowHidePassword.setOnClickListener {
            binding.etPassword.showHidePassword { isShown ->
                if (isShown) {
                    binding.ivShowHidePassword.setImageResource(R.drawable.ic_ios)
                } else {
                    binding.ivShowHidePassword.setImageResource(R.drawable.ic_android)
                }
            }
        }
    }

    private fun verifySignInAndLinkAccounts(isGoogle:Boolean) {
        firebaseAuthHelper.signInWithEmailAndPassword(
            email,
            binding.etPassword.text.toString()
        ) { isSuccessful ->
            if (isSuccessful) {
                toast("SigninSuccessful")
                if (isGoogle){
                    signInWithGoogle()
                } else{
                    FirebaseAuthHelper.facebookLoginResult?.accessToken?.token?.also {
                        linkFacebookAccount(it)
                    }
                }
            } else {
                toast(get("biletdukkani_pnrsearch_dialog_label_pnr_not_found_description"))
            }
        }
    }

    private fun signInWithFacebook() {
        firebaseAuthHelper.facebookLoginManager.logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )
    }

    private fun signInWithGoogle() {
        val signInIntent = firebaseAuthHelper.googleSignInClient.signInIntent
        registeredActivityForGoogleSignInResult.launch(signInIntent)
    }

    private fun linkGoogleAccount(token: String){
        val credential = GoogleAuthProvider.getCredential(
            token,
            null
        )
        firebaseAuthHelper.linkCredential(credential) { isSuccessful ->
            if (isSuccessful) {
                toast("linkSuccessful")
                PrefUtils.isLogin = true
                requireActivity().finish()
            } else {
                toast("linkFailed")
            }
        }
    }

    private fun onGoogleSignInActivityResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                when {
                    //Google girişi yapılacaksa
                    currentProvider == AuthProviders.Google.value && newProvider == AuthProviders.Email.value -> {
                        firebaseAuthHelper.firebaseAuthWithGoogle { isSuccessful ->
                            if (isSuccessful) {
                                toast(firebaseAuthHelper.googleSignInUser?.displayName.toString())
                                PrefUtils.isLogin = true
                                findNavController().navigateUp()
                                requireActivity().finish()
                            } else {
                                toast("googleSignInError")
                            }
                        }
                    }
                    //Google hesabı email ile bağlanacaksa
                    currentProvider == AuthProviders.Email.value && newProvider == AuthProviders.Google.value -> {
                        if (firebaseAuthHelper.googleSignInUser?.email == firebaseAuthHelper.firebaseUser?.email) {
                            firebaseAuthHelper.googleSignInUser?.idToken?.also {
                                linkGoogleAccount(it)
                            }
                        } else {
                            toast(get("biletdukkani_login_alert_please_select_x_account").replace("#{mail}",email))
                            firebaseAuthHelper.signOut()
                        }
                    }
                }

            } else {
                toast("googleSignInError")
            }
        }
    }

    private fun linkFacebookAccount(token:String){
        val credential = FacebookAuthProvider.getCredential(token)
        firebaseAuthHelper.linkCredential(credential) { isSuccessful ->
            if (isSuccessful) {
                toast("linkSuccessful")
                PrefUtils.isLogin = true
                requireActivity().finish()
            } else {
                toast("linkFailed")
            }
        }
    }

    private fun initFacebookLogin() {
        facebookCallbackManager = CallbackManager.Factory.create()
        firebaseAuthHelper.facebookLoginManager.registerCallback(
            facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    FirebaseAuthHelper.facebookLoginResult = loginResult
                    when {
                        //Facebook girişi yapılacaksa
                        currentProvider == AuthProviders.Facebook.value && newProvider == AuthProviders.Email.value -> {
                            firebaseAuthHelper.firebaseAuthWithFacebook { isSuccessful ->
                                if (isSuccessful) {
                                    PrefUtils.isLogin = true
                                    findNavController().navigateUp()
                                    requireActivity().finish()
                                } else {
                                    toast("facebookLoginError")
                                }
                            }
                        }
                        //Facebook hesabı email ile bağlanacaksa
                        currentProvider == AuthProviders.Email.value && newProvider == AuthProviders.Facebook.value -> {
                            FirebaseAuthHelper.facebookLoginResult?.accessToken?.token?.also {
                                linkFacebookAccount(it)
                            }
                        }
                    }
                }

                override fun onCancel() = Unit

                override fun onError(exception: FacebookException) = Unit
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDetach() {
        super.onDetach()
      //  (requireActivity() as LoginActivity).appbarHelper.isShowAppbar(false)
    }

    override fun createBinding(): ContinueWithSocialFragmentBinding {
        return ContinueWithSocialFragmentBinding.inflate(layoutInflater)
    }

}