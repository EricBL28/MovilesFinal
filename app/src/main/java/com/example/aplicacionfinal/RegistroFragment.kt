package com.example.aplicacionfinal

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.aplicacionfinal.databinding.FragmentRegistroBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.getValue


class RegistroFragment : Fragment() {
    private lateinit var binding: FragmentRegistroBinding
    private lateinit var auth: FirebaseAuth
    private val registerViewModel: RegisterViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()

        Glide.with(binding.root.context)
            .load(R.drawable.usuario) // URL de la imagen
            .placeholder(R.drawable.usuario) // Imagen mientras se carga
            .into(binding.imageLogin) // imagen xml

        // Observamos los errores de cada campo
        registerViewModel.emailError.observe(viewLifecycleOwner, Observer { error ->
            binding.EmailReg.error = error
        })

        registerViewModel.passwordError.observe(viewLifecycleOwner, Observer { error ->
            binding.password.error = error

        })

        registerViewModel.dateError.observe(viewLifecycleOwner, Observer { error ->
            binding.textoFechaSeleccionada.error = error

        })


        binding.EmailReg.editText?.addTextChangedListener {
            registerViewModel.setEmail(it.toString())
        }

        binding.password.editText?.addTextChangedListener {
            registerViewModel.setPassword(it.toString())
        }

        binding.textoFechaSeleccionada.addTextChangedListener {
            registerViewModel.setDate(it.toString())
        }

        //Fecha nacimiento----------------------------------------------------------------

        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            //le paso los datos al formato en sí y lo guardo
            val fechaFormateada = getString(R.string.fechaFormato,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR))

            //se le aplica el formato con datos de usuario
            binding.textoFechaSeleccionada.text = fechaFormateada

        }

        binding.botonSeleccionarFecha.setOnClickListener{

            showDatePicker(calendar, dateSetListener)
        }
        binding.botonRegistrar.setOnClickListener{

            if (registerViewModel.validateOnSubmit()) {
                val credenciales = validarCredenciales()

                if(credenciales != null)//la contraseña y el usuario es correcto
                {
                    crearCuenta(
                        binding.EmailReg.editText?.text.toString(),
                        binding.password.editText?.text.toString(),
                        binding.textoFechaSeleccionada.text.toString(),
                    )
                }
            } else {

                Snackbar.make(it, "ERROR generando la cuenta", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.botonAtras.setOnClickListener {

            findNavController().navigate(R.id.action_ResgistroFragment_to_LoginFragment)

        }


        binding.botonGoogle.setOnClickListener{

        }


    }
    //FUNCIONES-----------------------------------------------------------------------------------------
    private fun showDatePicker(calendar: Calendar, dateSetListener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    fun crearCuenta(email: String, password: String, fecha: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val db = FirebaseFirestore.getInstance()

                        // Crear un array de enteros vacío
                        val arrayFav = arrayListOf<Int>()

                        // Datos del usuario
                        val userData = hashMapOf(
                            "correo" to email,
                            "contraseña" to password,
                            "fecha" to fecha,
                            "fav" to arrayFav
                        )

                        // Guardar los datos en Firestore
                        db.collection("usuarios").document(email)
                            .set(userData)
                            .addOnSuccessListener {
                                findNavController().navigate(R.id.action_ResgistroFragment_to_ScaffolgFragment)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error al guardar usuario: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    Snackbar.make(binding.root, "Error al crear cuenta: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
    }


    private fun validarCredenciales(): Pair<String, String>? {
        //he tenido que añadir binding.root para que me pillase el findViewById
        val usuario = binding.EmailReg.editText?.text.toString().trim()
        val contrasenia = binding.password.editText?.text.toString().trim()
        val contraseniaRepetir = binding.password2.editText?.text.toString().trim()
        return when { //compruebo que tengan un formato valido
            !esCorreoValido(usuario) -> {
                mostrarAlertaCorreoInvalido()
                null
            }
            !esContraseniaValida(contrasenia) -> {
                mostrarAlertaContraseniaInvalida()
                null
            }!esContraseniaValida(contraseniaRepetir) -> {
                mostrarAlertaContraseniaInvalida()
                null
            }

            contrasenia != contraseniaRepetir -> {
                mostrarAlertaConstrasenaNoIgual()
                null
            }
            else -> Pair(usuario, contrasenia)
        }
    }
    // Función para validar el correo electrónico
    private fun esCorreoValido(correo: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return correo.matches(emailRegex.toRegex())
    }
    // Función para validar la contraseña
    private fun esContraseniaValida(contrasenia: String): Boolean {
        return contrasenia.length in 4..10
    }

    private fun mostrarAlerta(titulo: String, mensaje: String) {
        val ok = getString(R.string.ok)
        /*uso require context en vez de this, y salta excepcion si el fragment no está vinculado a un activity*/
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton(ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
    private fun mostrarAlertaCorreoInvalido() {
        mostrarAlerta(
            titulo = getString(R.string.nocorreo),
            mensaje = getString(R.string.nuevocorreo)
        )
    }
    private fun mostrarAlertaContraseniaInvalida() {
        mostrarAlerta(
            titulo = getString(R.string.nocontra),
            mensaje = getString(R.string.nuevacontra)
        )
    }

    private fun mostrarAlertaConstrasenaNoIgual() {
        mostrarAlerta(
            titulo = "Contrasñeas desigual",
            mensaje = getString(R.string.contraCoinci)
        )
    }
}