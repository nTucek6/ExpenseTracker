package com.example.expensetracker.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.R
import com.example.expensetracker.firebase.google_auth.GoogleAuthClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    lateinit var singInBtn: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val googleAuthClient = GoogleAuthClient(requireContext())

        singInBtn = view.findViewById(R.id.btn_login_out)

        lifecycleScope.launch {
            googleAuthClient.isSignedIn.collect { isSignedInState ->
                singInBtn.text = if (isSignedInState) "Sign Out" else "Sign in With Google"
                singInBtn.setOnClickListener {
                    lifecycleScope.launch {
                        toggleButtonDisable()
                        try {
                            if (isSignedInState) {
                                googleAuthClient.signOut()

                            } else {
                                googleAuthClient.signIn()
                            }
                        } finally {
                            toggleButtonDisable()
                        }
                    }
                }
            }
        }
    }

    private fun toggleButtonDisable() {
        singInBtn.isEnabled = !singInBtn.isEnabled
    }
}