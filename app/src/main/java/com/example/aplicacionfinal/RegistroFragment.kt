package com.example.aplicacionfinal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.aplicacionfinal.databinding.FragmentRegistroBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Calendar


class RegistroFragment : Fragment() {
    private lateinit var binding: FragmentRegistroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistroBinding.inflate(inflater, container, false)

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()



        binding.botonAtras.setOnClickListener {

            findNavController().navigate(R.id.action_ResgistroFragment_to_LoginFragment)

        }

        binding.botonGoogle.setOnClickListener {


        }


        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            binding.textoFechaSeleccionada.text = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        }

        binding.botonSeleccionarFecha.setOnClickListener{
            showDatePicker(calendar, dateSetListener)
        }

    }

    private fun showDatePicker(calendar: Calendar, dateSetListener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private fun registerWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        registerLauncher.launch(signInIntent)
    }

    private val registerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseRegisterWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("GoogleSignUp", "Error al registrarse con Google", e)
                Toast.makeText(requireContext(), "Error en Google Sign-Up", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseRegisterWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true

                    if (isNewUser) {
                        Toast.makeText(requireContext(), "¡Registro exitoso! ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Ya tienes cuenta, iniciando sesión...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("GoogleSignUp", "Error en Firebase: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Error registrando con Firebase", Toast.LENGTH_SHORT).show()
                }
            }
    }

}