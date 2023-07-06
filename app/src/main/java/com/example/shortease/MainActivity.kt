package com.example.shortease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object {
        const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //firebase stuff
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .requestScopes(Scope(YouTubeScopes.YOUTUBE_READONLY),Scope(YouTubeScopes.YOUTUBE_FORCE_SSL))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        setContent {
            ShortEaseTheme {
                navController = rememberNavController()
                    SetupNavGraph(
                        navController = navController,
                        startDestination = if(user != null) Screen.MyVideos.route else Screen.HomeScreen.route,
                        signInClicked = { signIn() },
                        signOutClicked = { signOut() }
                    )
                }
            }
        }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Button click listener


    private fun signOut() {
        // get the google account
        val googleSignInClient: GoogleSignInClient

        // configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Sign Out of all accounts
        firebaseAuth.signOut()
        googleSignInClient.signOut()
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Screen.HomeScreen.route)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign Out Failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    //sign in successful firebase authentication
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: Exception) {
                    //google failed sign in
                    Log.d("Sign in", "Google Sign In Failed ")
                }
            } else {
                Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show()
                Log.d("SignIN", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.MyVideos.route)
                } else {
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            }
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            fetchYouTubeChannelId(account.idToken!!)
        } else {
            Toast.makeText(this, "Failed to retrieve Google account information", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchYouTubeChannelId(idToken: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val httpTransport = NetHttpTransport()
                val jsonFactory = GsonFactory()

                val youtube = YouTube.Builder(httpTransport, jsonFactory, null)
                    .setApplicationName("YourAppName")
                    .build()

                val credential = GoogleCredential().setAccessToken(idToken)

                val channels = youtube.channels().list(mutableListOf("id"))
                    .setMine(true)
                    .setKey("API KEY")
                    .setFields("items(id)")
                    .setOauthToken(credential.accessToken)
                    .execute()

                val channelId = channels.items[0].id

                // Use the channelId as needed in your app
                Log.d("YouTube Channel ID", channelId)

                // Continue navigating to the desired screen, passing the channelId if needed
                navController.navigate(Screen.MyVideos.route)
            } catch (e: Exception) {
                // Handle any exceptions that occur during the YouTube channel ID retrieval
                Log.e("Fetch YouTube Channel ID", "Error: ${e.message}")
                Toast.makeText(this@MainActivity, "Failed to fetch YouTube channel ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}