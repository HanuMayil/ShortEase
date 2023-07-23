package com.example.shortease

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shortease.ui.theme.ShortEaseTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object {
        const val RC_SIGN_IN = 100
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //firebase stuff
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        setContent {
            clearCachedData(LocalContext.current)
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
    }

    private fun clearCachedData(context: Context) {
        val folder = File(context.filesDir, "videos")
        if (folder.exists() && folder.isDirectory) {
            val sub_folders =  folder.listFiles { file ->
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