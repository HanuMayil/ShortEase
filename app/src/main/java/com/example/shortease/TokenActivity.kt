package com.example.shortease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.example.shortease.auth.AuthStateManager
import com.example.shortease.auth.Configuration
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientAuthentication.UnsupportedAuthenticationMethod
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class TokenActivity : AppCompatActivity() {
    private var mAuthService: AuthorizationService? = null
    private var mStateManager: AuthStateManager? = null
    private val mUserInfoJson = AtomicReference<JSONObject?>()
    private var mExecutor: ExecutorService? = null
    private var mConfiguration: Configuration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateManager = AuthStateManager.getInstance(this)
        mExecutor = Executors.newSingleThreadExecutor()
        mConfiguration = Configuration.getInstance(this)
        val config = Configuration.getInstance(this)
        mAuthService = AuthorizationService(
            this,
            AppAuthConfiguration.Builder()
                .setConnectionBuilder(config.connectionBuilder)
                .build()
        )
        if (savedInstanceState != null) {
            try {
                mUserInfoJson.set(JSONObject(savedInstanceState.getString(KEY_USER_INFO)))
            } catch (ex: JSONException) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex)
            }
        }
    }

    @WorkerThread
    private fun getAccessToken(): String {
        var accessToken = ""
        mStateManager!!.current.performActionWithFreshTokens(mAuthService!!) { token: String?, _, _ ->
            accessToken = token ?: ""
        }
        return accessToken
    }

    override fun onStart() {
        super.onStart()
        if (mExecutor!!.isShutdown) {
            mExecutor = Executors.newSingleThreadExecutor()
        }
        if (mStateManager!!.current.isAuthorized) {
            var resultData = getAccessToken()
            if(resultData == "") {
                refreshAccessToken()
                resultData = getAccessToken()
            }
            val resultIntent = Intent().apply {
                putExtra("resultKey", resultData)
            }
            setResult(RC_AT_SUCCESS, resultIntent)
            finish()
        } else {
            // the stored AuthState is incomplete, so check if we are currently receiving the result of
            // the authorization flow from the browser.
            val response = AuthorizationResponse.fromIntent(intent)
            val ex = AuthorizationException.fromIntent(intent)
            if (response != null || ex != null) {
                mStateManager!!.updateAfterAuthorization(response, ex)
            }
            if (response != null && response.authorizationCode != null) {
                // authorization code exchange is required
                mStateManager!!.updateAfterAuthorization(response, ex)
                exchangeAuthorizationCode(response)
            } else if (ex != null) {
                displayMessage("Authorization flow failed: " + ex.message)
                setResult(RC_REAUTH)
                finish()
            } else {
                displayMessage("No authorization state retained - reauthorization required")
                setResult(RC_REAUTH)
                finish()
            }
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        if (mUserInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mAuthService!!.dispose()
        mExecutor!!.shutdownNow()
    }

    @MainThread
    private fun refreshAccessToken() {
        performTokenRequest(
            mStateManager!!.current.createTokenRefreshRequest()
        ) { tokenResponse: TokenResponse?, authException: AuthorizationException? ->
            handleAccessTokenResponse(
                tokenResponse,
                authException
            )
        }
    }

    @MainThread
    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        performTokenRequest(
            authorizationResponse.createTokenExchangeRequest(),
            this::handleCodeExchangeResponse)
    }

    @MainThread
    private fun performTokenRequest(
        request: TokenRequest,
        callback: TokenResponseCallback
    ) {
        val clientAuthentication: ClientAuthentication = try {
            mStateManager!!.current.clientAuthentication
        } catch (ex: UnsupportedAuthenticationMethod) {
            Log.d(
                TAG, "Token request cannot be made, client authentication for the token "
                        + "endpoint could not be constructed (%s)", ex
            )
            return
        }
        mAuthService!!.performTokenRequest(
            request,
            clientAuthentication,
            callback
        )
    }

    @WorkerThread
    private fun handleAccessTokenResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?
    ) {
        mStateManager!!.updateAfterTokenResponse(tokenResponse, authException)
    }

    @WorkerThread
    private fun handleCodeExchangeResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?
    ) {
        mStateManager!!.updateAfterTokenResponse(tokenResponse, authException)
        if (!mStateManager!!.current.isAuthorized) {
            val message = ("Authorization Code exchange failed" + if (authException != null) authException.error else "")
            Log.d(TAG, message)
        }
        val resultData = getAccessToken()
        val resultIntent = Intent().apply {
            putExtra("resultKey", resultData)
        }
        setResult(RC_AT_SUCCESS, resultIntent)
        finish()
    }

    companion object {
        private const val TAG = "TokenActivity"
        private const val KEY_USER_INFO = "userInfo"
        private const val RC_AT_SUCCESS = 300
        private const val RC_REAUTH = 400
    }

    private fun displayMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}