package com.example.shortease

import AccessTokenHolder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.auth.AuthStateManager
import com.example.shortease.auth.Configuration
import com.example.shortease.ui.theme.ShortEaseTheme
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.RegistrationRequest
import net.openid.appauth.RegistrationResponse
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.browser.AnyBrowserMatcher
import net.openid.appauth.browser.BrowserMatcher
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class LoginActivity : AppCompatActivity() {
    private lateinit var navController: NavHostController

    private var mAuthService: AuthorizationService? = null
    private var mAuthStateManager: AuthStateManager? = null
    private var mConfiguration: Configuration? = null

    private val mClientId = AtomicReference<String>()
    private val mAuthRequest = AtomicReference<AuthorizationRequest>()
    private val mAuthIntent = AtomicReference<CustomTabsIntent>()
    private var mAuthIntentLatch = CountDownLatch(1)
    private var mExecutor: ExecutorService? = null

    private var mUsePendingIntents = false

    private val mBrowserMatcher: BrowserMatcher = AnyBrowserMatcher.INSTANCE

    private var channelId = ""

    companion object {
        private const val TAG = "LoginActivity"
        private const val RC_AUTH = 100
        private const val RC_START = 200
        private const val RC_AT_SUCCESS = 300
        private const val RC_REAUTH = 400
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mExecutor = Executors.newSingleThreadExecutor()
        mAuthStateManager = AuthStateManager.getInstance(this)
        mConfiguration = Configuration.getInstance(this)

        // already aunthenticated
        if (mAuthStateManager!!.current.isAuthorized) {
            startActivityForResult(Intent(this, TokenActivity::class.java), 300)
        }

        setContent {
            ShortEaseTheme {
                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    startDestination = Screen.HomeScreen.route,
                    signInClicked = { startAuth() },
                    signOutClicked = { signOut() }
                )
            }
        }

        mExecutor!!.submit(Runnable { this.initializeAppAuth() })
    }

    @MainThread
    fun startAuth() {
        mUsePendingIntents = false
        mExecutor!!.submit { this.doAuth() }
    }

    @WorkerThread
    private fun doAuth() {
        try {
            mAuthIntentLatch.await()
        } catch (ex: InterruptedException) {
            Log.w(TAG, "Interrupted while waiting for auth intent")
        }

        val intent = mAuthService!!.getAuthorizationRequestIntent(
            mAuthRequest.get(),
            mAuthIntent.get()
        )
        startActivityForResult(intent, RC_AUTH)

    }

    override fun onStart() {
        super.onStart()
        if (mExecutor!!.isShutdown) {
            mExecutor = Executors.newSingleThreadExecutor()
        }
    }

    override fun onStop() {
        super.onStop()
        mExecutor!!.shutdownNow()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAuthService?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RC_REAUTH) {
            signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else if(resultCode == RC_AT_SUCCESS) {
            AccessTokenHolder.accessToken = data?.getStringExtra("resultKey")
            lifecycleScope.launch {
                fetchYouTubeChannelId(AccessTokenHolder.accessToken)
                navController.navigate("my_videos?channelId=$channelId")
            }
        } else {
            val intent = Intent(this, TokenActivity::class.java)
            intent.putExtras(data!!.extras!!)
            startActivityForResult(intent, RC_START)
        }
    }

    @WorkerThread
    private fun initializeAppAuth() {
        Log.i(TAG, "Initializing AppAuth")
        recreateAuthorizationService()
        if (mAuthStateManager!!.current.authorizationServiceConfiguration != null) {
            // configuration is already created, skip to client initialization
            Log.i(TAG, "auth config already established")
            initializeClient()
            return
        }

        // if we are not using discovery, build the authorization service configuration directly
        // from the static configuration values.
        if (mConfiguration!!.discoveryUri == null) {
            Log.i(TAG, "Creating auth config from res/raw/auth_config.json")
            val config = AuthorizationServiceConfiguration(
                mConfiguration!!.authEndpointUri!!,
                mConfiguration!!.tokenEndpointUri!!,
                mConfiguration!!.registrationEndpointUri,
                mConfiguration!!.endSessionEndpoint
            )
            mAuthStateManager!!.replace(AuthState(config))
            initializeClient()
            return
        }

        Log.i(TAG, "Retrieving OpenID discovery doc")
        AuthorizationServiceConfiguration.fetchFromUrl(
            mConfiguration!!.discoveryUri!!,
            { config: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                this.handleConfigurationRetrievalResult(
                    config,
                    ex
                )
            },
            mConfiguration!!.connectionBuilder
        )
    }

    @MainThread
    private fun handleConfigurationRetrievalResult(
        config: AuthorizationServiceConfiguration?,
        ex: AuthorizationException?
    ) {
        if (config == null) {
            Log.i(TAG, "Failed to retrieve discovery document", ex)
            return
        }
        Log.i(TAG, "Discovery document retrieved")
        mAuthStateManager!!.replace(AuthState(config))
        mExecutor!!.submit { initializeClient() }
    }

    @WorkerThread
    private fun initializeClient() {
        if (mConfiguration!!.clientId != null) {
            Log.i(TAG, "Using static client ID: " + mConfiguration!!.clientId)
            mClientId.set(mConfiguration!!.clientId)
            runOnUiThread { this.initializeAuthRequest() }
            return
        }
        val lastResponse = mAuthStateManager!!.current.lastRegistrationResponse
        if (lastResponse != null) {
            Log.i(TAG, "Using dynamic client ID: " + lastResponse.clientId)
            mClientId.set(lastResponse.clientId)
            runOnUiThread { this.initializeAuthRequest() }
            return
        }

        Log.i(TAG, "Dynamically registering client")
        val registrationRequest = RegistrationRequest.Builder(
            mAuthStateManager!!.current.authorizationServiceConfiguration!!,
            listOf(mConfiguration!!.redirectUri)
        )
            .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
            .build()
        mAuthService!!.performRegistrationRequest(
            registrationRequest
        ) { response: RegistrationResponse?, ex: AuthorizationException? ->
            this.handleRegistrationResponse(
                response,
                ex
            )
        }
    }

    @MainThread
    private fun handleRegistrationResponse(
        response: RegistrationResponse?,
        ex: AuthorizationException?
    ) {
        mAuthStateManager!!.updateAfterRegistration(response, ex)
        if (response == null) {
            Log.i(TAG, "Failed to dynamically register client", ex)
            return
        }
        Log.i(TAG, "Dynamically registered client: " + response.clientId)
        mClientId.set(response.clientId)
        initializeAuthRequest()
    }

    @MainThread
    private fun initializeAuthRequest() {
        createAuthRequest()
        warmUpBrowser()
    }

    private fun createAuthRequest() {
        val authRequestBuilder = AuthorizationRequest.Builder(
            mAuthStateManager!!.current.authorizationServiceConfiguration!!,
            mClientId.get(),
            ResponseTypeValues.CODE,
            mConfiguration!!.redirectUri
        )
            .setScope(mConfiguration!!.scope)
        mAuthRequest.set(authRequestBuilder.build())
    }

    private fun warmUpBrowser() {
        mAuthIntentLatch = CountDownLatch(1)
        mExecutor!!.execute {
            Log.i(TAG, "Warming up browser instance for auth request")
            val intentBuilder =
                mAuthService!!.createCustomTabsIntentBuilder(mAuthRequest.get().toUri())
            mAuthIntent.set(intentBuilder.build())
            mAuthIntentLatch.countDown()
        }
    }

    private fun recreateAuthorizationService() {
        if (mAuthService != null) {
            Log.i(TAG, "Discarding existing AuthService instance")
            mAuthService!!.dispose()
        }
        mAuthService = createAuthorizationService()
        mAuthRequest.set(null)
        mAuthIntent.set(null)
    }

    private fun createAuthorizationService(): AuthorizationService? {
        Log.i(TAG, "Creating authorization service")
        val builder = AppAuthConfiguration.Builder()
        builder.setBrowserMatcher(mBrowserMatcher)
        builder.setConnectionBuilder(mConfiguration!!.connectionBuilder)
        return AuthorizationService(this, builder.build())
    }

    @MainThread
    private fun signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        val currentState: AuthState = mAuthStateManager!!.current
        val clearedState = AuthState(currentState.authorizationServiceConfiguration!!)
        if (currentState.lastRegistrationResponse != null) {
            clearedState.update(currentState.lastRegistrationResponse)
        }
        mAuthStateManager!!.replace(clearedState)
        val mainIntent = Intent(this, LoginActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainIntent)
        finish()
    }

    private suspend fun fetchYouTubeChannelId(accessToken: String?) {
        return withContext(Dispatchers.IO) {
            try {
                val httpTransport = NetHttpTransport()
                val jsonFactory = GsonFactory()

                val youtube = YouTube.Builder(httpTransport, jsonFactory, null)
                    .setApplicationName("YourAppName")
                    .build()

                val credential = GoogleCredential().setAccessToken(accessToken)

                val channels = youtube.channels().list(mutableListOf("id"))
                    .setMine(true)
                    .setKey("AIzaSyCZ1aVkQw5j_ljA-AesWfHh0c6lnGQIq-A")
                    .setFields("items(id)")
                    .setAccessToken(credential.accessToken)
                    .execute()

                channelId = channels.items[0].id
            } catch (e: Exception) {
                // Handle any exceptions that occur during the YouTube channel ID retrieval
                Log.e("Fetch YouTube Channel ID", "Error: ${e.message}")
            }
        }
    }

    private fun clearCachedData(context: Context) {
        val folder = File(context.filesDir, "videos")
        if (folder.exists() && folder.isDirectory) {
            val sub_folders = folder.listFiles { file ->
                file.isDirectory
            }
            sub_folders?.forEach { sub_folder ->
                val thumbnailFile = File(sub_folder, "thumbnail.jpg")
                if (!thumbnailFile.exists()) {
                    sub_folder.deleteRecursively()
                }
            }
        }
    }
}