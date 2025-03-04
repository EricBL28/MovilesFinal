@file:Suppress("DEPRECATION")

package com.example.aplicacionfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aplicacionfinal.databinding.FragmentLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Si ya está autenticado, redirige a la pantalla principal
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_LoginFragment_to_ScaffolgFragment)
            return
        }


        // Acción al hacer clic en el botón de login
        binding.loginButton.setOnClickListener {
            val email = binding.TextoEmail.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Por favor ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción al hacer clic en el texto para ir al registro
        binding.registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_ResgistroFragment)
        }

        // Acción para loguear con Google
        binding.googleButton.setOnClickListener {
            signInWithGoogle()
        }
    }


    // Función para iniciar sesión con email y contraseña
    private fun loginUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->

            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_LoginFragment_to_ScaffolgFragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error de autenticación: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signInWithGoogle() {
        val auth = FirebaseAuth.getInstance() //si no inicio esto aquí, no me inicia sesiñón

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("918370798893-v4n2c03doouvbaqgfpkaarm8eaufe760.apps.googleusercontent.com")//id despues de sha1
            //.setNonce(hashedNonce)
            .setAutoSelectEnabled(false)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            credentialManager = CredentialManager.create(context = requireContext())
            try {

                val result = credentialManager.getCredential(context = requireContext(), request = request)
                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)


                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                if (authResult != null) {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(requireContext(), "Login exitoso", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_LoginFragment_to_ScaffolgFragment)
                    }

                } else {
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(requireContext(), "Error en el login", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: GetCredentialException) {
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}


