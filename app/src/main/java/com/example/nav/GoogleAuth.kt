package com.example.nav

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuth(private val context: Context) {
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    private var auth= FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    private fun firebaseAuthWithGoogle(activity: MainActivity, acct: GoogleSignInAccount, onCompleteListener: (user: FirebaseUser?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                onCompleteListener.invoke(auth.currentUser)
            } else {
                onCompleteListener.invoke(null)
                logD("GoogleAuth - firebaseAuthWithGoogle : ${task.exception}")
            }
        }
    }

    fun signIn(activity: MainActivity, onCompleteListener: (user: FirebaseUser?) -> Unit) {
        val signInIntent = googleSignInClient.signInIntent
        activity.setOnActivityResultListener { requestCode, _, data ->
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(activity, account!!, onCompleteListener)
                } catch (e: ApiException) {
                    logD("GoogleAuth - SignIn : $e")
                }
            }
        }
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut(onCompleteListener: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
           onCompleteListener.invoke()
        }
    }

    fun revokeAccess() {
//        auth.signOut()
//        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
//            updateUI(null)
//        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}