package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*
/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */

private const val SIGN_IN = 1

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("<<AuthActivity>>", "<<2>> onCreate: ")

        val firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {

            Log.d("<<AuthActivity>> ", "<<3>> firebaseAuth.currentUser " + firebaseAuth.currentUser)
            navigateToRemindersActivity()
            return
        }

        setContentView((R.layout.activity_authentication))
        findViewById<View>(R.id.auth_button).setOnClickListener { onAuthButtonClicked() }

    }

    private fun navigateToRemindersActivity() {
        // Step 1 - Navigate the user to the reminders screen. If there is a current user id
        // then we flow here.... And start an activity with an intent here .. to RemindersActivity.
        Log.d("<<AuthActivity>> ", "<<3>> This step is sending me to RemindersActivity ")
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()
    }

    private fun onAuthButtonClicked() {
        Log.d("<<AuthActivity>>", "<<4>> onAuthButtonClicked: ")
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build()
                    )
                )
                .setAuthMethodPickerLayout(
                    AuthMethodPickerLayout
                        .Builder(R.layout.login)
                        .setGoogleButtonId(R.id.google_button)
                        .setEmailButtonId(R.id.email_button)
                        .build()
                )
                .setTheme(R.style.AppTheme)
                .build(), SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        Log.d("<<AuthActivity>>", "onActivityResult: ")
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d(
                "<<AuthActivity>>",
                "onActivityResult: " + "${FirebaseAuth.getInstance().currentUser?.displayName}!"
            )
            val intent = Intent(this, RemindersActivity::class.java)
            startActivity(intent)
        } else {
            Log.d("<<AuthActivity>>", "Successful sign in $response?.error.errorCode}" )
        }

        if (requestCode != SIGN_IN) {
            Log.d("<<AuthActivity>> ", "requestCode = " + requestCode)
            return
        }

        if (resultCode == RESULT_OK) {
            Log.d("<<AuthActivity>> ", "resultCode " + resultCode)
            navigateToRemindersActivity()
            return
        }


    }

}
