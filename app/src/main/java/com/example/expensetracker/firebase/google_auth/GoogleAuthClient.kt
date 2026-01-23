package com.example.expensetracker.firebase.google_auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.expensetracker.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(private val context: Context) {

    private val tag = "GoogleAuthClient: "
    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _isSignedIn = MutableStateFlow(isSingedIn())
    val isSignedIn: StateFlow<Boolean> = _isSignedIn.asStateFlow()

    private fun updateSignInState(signedIn: Boolean) {
        _isSignedIn.value = signedIn
    }

    fun isSingedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            Log.d(tag, "already singed in!")
            return true
        }
        return false
    }

    suspend fun signIn(): Boolean {
        if (isSingedIn()) {
            return true;
        }
        try {
            val result = buildCredentialRequest()
            val handleResult: Boolean = handleSignIn(result)
            updateSignInState(handleResult)
            return handleResult
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Log.d(tag, "sinIn error: ${e.message}")
            return false
        }

    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Boolean {
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        )

            try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Log.d(tag, "name: ${tokenCredential.displayName}")
                Log.d(tag, "email: ${tokenCredential.id}")
                Log.d(tag, "image: ${tokenCredential.profilePictureUri}")

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)

                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user != null
            } catch (e: GoogleIdTokenParsingException) {
                Log.d(tag, "GoogleIdTokenParsingException: ${e.message}")
                updateSignInState(false)
                return false
            }
        else {
            Log.d(tag, "Credential is not GoogleIdTokenCredential")
            return false
        }
    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_SERVER_CLIENT_KEY)
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        return credentialManager.getCredential(request = request, context = context)
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
        updateSignInState(false)
    }

    fun getUser(): FirebaseUser? {
        if (isSingedIn()) {
            return firebaseAuth.currentUser
        }
        return null
    }
}