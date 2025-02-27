@file:Suppress("DEPRECATION")

package com.example.aplicacionfinal
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplicacionfinal.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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

        // Inicializa el ProgressBar
        progressBar = binding.progressBar

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("trabajomoviles-43cae"
            )  // Reemplaza con tu Client ID de Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

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
            loginGoogle()
        }
    }

    private fun loginGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Manejo del resultado de Google Sign-In
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("Google Sign-In", "Error al iniciar sesión con Google", e)
                Toast.makeText(requireContext(), "Error en Google Sign-In", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (task.result.additionalUserInfo?.isNewUser == true) {
                        //Usuario nuevo: podemos guardar datos adicionales en Firestore si queremos
                        Toast.makeText(requireContext(), "Bienvenido, nuevo usuario ${user?.displayName}!", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), "Bienvenido de nuevo, ${user?.displayName}!", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().navigate(R.id.action_LoginFragment_to_ScaffolgFragment)
                } else {
                    Toast.makeText(requireContext(), "Error autenticando con Firebase", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para iniciar sesión con email y contraseña
    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
            progressBar.visibility = View.GONE

            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_LoginFragment_to_ScaffolgFragment)
            } else {
                Toast.makeText(requireContext(), "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


