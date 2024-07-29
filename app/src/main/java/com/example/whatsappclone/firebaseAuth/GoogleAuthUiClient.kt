package com.example.whatsappclone.firebaseAuth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.service.autofill.UserData
import com.example.whatsappclone.R
import com.example.whatsappclone.models.SignInResult
import com.example.whatsappclone.models.Userdata
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context : Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth


    suspend fun signIn(): IntentSender? {
        val result = try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch(e:Exception){
            e.printStackTrace()
            if(e is CancellationException)
                throw e
            else
                null
        }
        return result?.pendingIntent?.intentSender
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun signInWithIntent(intent:Intent): SignInResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)

        return try{
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run{
                    Userdata(
                        userId = user.uid,
                        username = user.displayName.toString(),
                        image = user.photoUrl.toString(),
                        email = user.email.toString()
                    )
                },
                error = null
            )
        }catch (e : Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                error = e.message
            )
        }
    }

    private fun buildSignInRequest() : BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}