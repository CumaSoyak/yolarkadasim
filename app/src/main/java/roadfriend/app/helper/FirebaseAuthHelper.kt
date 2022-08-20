package roadfriend.app.helper

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf

import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.LoggingBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.json.JSONObject
import roadfriend.app.BuildConfig
import roadfriend.app.enums.AuthProviders
import roadfriend.app.utils.PrefUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseAuthHelper(val context: Context) {
    private val mAuth = FirebaseAuth.getInstance()
    fun isLoggedIn(): Boolean {
        getToken {
            println("token: $it")
        }
        PrefUtils.isLogin = firebaseUser != null
        return firebaseUser != null
    }

    val firebaseUser: FirebaseUser?
        get() {
            return mAuth.currentUser
        }

    val googleSignInUser: GoogleSignInAccount?
        get() {
            return GoogleSignIn.getLastSignedInAccount(context)
        }

    val facebookLoginManager = LoginManager.getInstance()

    val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("BuildConfig.DEFAULT_WEB_TOKEN_ID")
            .requestServerAuthCode("BuildConfig.DEFAULT_WEB_TOKEN_ID")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        if (false) {//!BuildConfig.IsProd
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
    }

    fun getToken(isForceRefreshToken: Boolean = false, action: ((token: String?) -> Unit)?) {
        FirebaseAuthHelper(context).firebaseUser?.getIdToken(isForceRefreshToken)
            ?.addOnCompleteListener {
                action?.invoke(it.result?.token)
                println(it.result?.token)
            }
    }

    suspend fun getToken() = suspendCoroutine<GetTokenResult?> { continuation ->
        firebaseUser?.getIdToken(true)?.addOnCompleteListener {
            if (it.isSuccessful) {
                continuation.resume(it.result)
            } else {
                continuation.resume(null)
            }
        }
    }

    fun fetchSignInMethods(
        email: String,
        action: ((isSuccessful: Boolean, signInMethods: List<String>) -> Unit)?
    ) {
        mAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                action?.invoke(task.isSuccessful, task.result?.signInMethods ?: listOf())
                println(task.result?.signInMethods)
            }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        action: ((isSuccessful: Boolean) -> Unit)?
    ) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                action?.invoke(task.isSuccessful)
            }
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        action: ((isSuccessful: Boolean) -> Unit)?
    ) {
        val credential = EmailAuthProvider.getCredential(email, password)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }

    }

    fun changeUserPassword(
        newPassword: String,
        action: ((isSuccessful: Boolean) -> Unit)?
    ) {
        val newCredential = EmailAuthProvider.getCredential(firebaseUser?.email ?: "", newPassword)

        if (firebaseUser.hasEmailCredential()) {
            firebaseUser?.updatePassword(newPassword)?.addOnCompleteListener { task ->
                action?.invoke(task.isSuccessful)
                if (task.isSuccessful)
                    reAuthenticateWithCredential(newCredential, null)
            }
        } else {
            action?.invoke(false)
        }
    }

    fun reAuthenticateWithCredential(
        credential: AuthCredential,
        action: ((isSuccessful: Boolean) -> Unit)?
    ) {
        firebaseUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }
    }

    fun reAuthenticateWithCredential(
        password: String,
        action: ((isSuccessful: Boolean) -> Unit)?
    ) {
        val credential = EmailAuthProvider.getCredential(firebaseUser?.email ?: "", password)
        firebaseUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }
    }

    fun firebaseAuthWithGoogle(action: ((isSuccessful: Boolean) -> Unit)?) {
        val credential = GoogleAuthProvider.getCredential(googleSignInUser?.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }
    }

    fun firebaseAuthWithFacebook(action: ((isSuccessful: Boolean) -> Unit)?) {
        facebookLoginResult?.accessToken?.token?.also {
            val credential = FacebookAuthProvider.getCredential(it)
            mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                action?.invoke(task.isSuccessful)
            }
        }
    }

    fun sendPasswordResetEmail(email: String, action: ((isSuccessful: Boolean) -> Unit)?) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                action?.invoke(task.isSuccessful)
            }
    }

    fun resetPassword(code: String, password: String, action: ((isSuccessful: Boolean) -> Unit)?) {
        mAuth.confirmPasswordReset(code, password).addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }
    }

    fun linkCredential(credential: AuthCredential, action: ((isSuccessful: Boolean) -> Unit)?) {
        firebaseUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }
    }

    fun sendVerificationEmail(action: ((isSuccessful: Boolean) -> Unit)?) {
        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            action?.invoke(task.isSuccessful)
        }
    }

    suspend fun requestForFacebookEmail() = suspendCoroutine<JSONObject?> {
        val request = GraphRequest.newMeRequest(
            facebookLoginResult?.accessToken
        ) { `object`, response ->
            Log.v("LoginActivity Response ", response.toString())
            try {
                facebookEmail = `object`.getString("email")
                facebookName = `object`.getString("first_name")
                facebookSurname = `object`.getString("last_name")
                Log.v("Email = ", " $facebookEmail")
                it.resume(`object`)
            } catch (e: JSONException) {
                e.printStackTrace()
                it.resume(null)
            }
        }
        request.parameters = bundleOf("fields" to "id, first_name, last_name, email")
        request.executeAsync()


    }

    fun signOut() {
        mAuth.signOut()
        googleSignInClient.signOut()
    }

    companion object {
        var facebookLoginResult: LoginResult? = null
        var facebookName: String? = null
        var facebookSurname: String? = null
        var facebookEmail: String? = null
    }
}

fun FirebaseUser?.hasEmailCredential(): Boolean {
    return this?.providerData?.any { it.providerId == AuthProviders.Email.value } == true
}