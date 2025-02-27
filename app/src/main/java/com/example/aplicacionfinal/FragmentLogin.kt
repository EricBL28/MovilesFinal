package com.example.aplicacionfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aplicacionfinal.databinding.FragmentLoginBinding

import com.google.firebase.auth.FirebaseAuth

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var progressBar: ProgressBar
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

        // Inicializa el ProgressBar
        progressBar = binding.progressBar

        // Acción al hacer clic en el botón de login
        binding.loginButton.setOnClickListener {
            val email = binding.TextoEmail.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Verificar si los campos no están vacíos
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

        // Acción para loguear con google
        binding.googleButton.setOnClickListener{
            loginGoogle()
        }
    }
    // Función para iniciar sesión
    private fun loginUser(email: String, password: String) {
        // Muestra el ProgressBar mientras se intenta autenticar al usuario
        progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                // Ocultar el ProgressBar después de intentar el login
                progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    // Si el login fue exitoso, redirige a la siguiente pantalla
                    findNavController().navigate(R.id.action_LoginFragment_to_ScaffolgFragment)
                } else {
                    // Si el login falla, muestra un mensaje de error
                    Toast.makeText(requireContext(), "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    private fun loginGoogle(){

    }

}
