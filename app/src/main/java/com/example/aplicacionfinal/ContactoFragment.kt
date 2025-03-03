package com.example.aplicacionfinal

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aplicacionfinal.databinding.FragmentContactoBinding
import com.google.android.material.snackbar.Snackbar



class ContactoFragment : Fragment() {
    private lateinit var binding: FragmentContactoBinding
    private val lOCATIONPERMISSIONREQUESTCODE = 124
    private val cALLPHONEPERMISSIONREQUEST = 123

    private val permisoDenegado = "El permiso ha sido rechazado previamente, debes activarlo desde ajustes"
    private val appNoEncontrada = "No se encontró una aplicación para realizar esta acción"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentContactoBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding.botonllamar.setOnClickListener{
            hacerLlamada()
        }

        binding.botonWhatsapp.setOnClickListener{
            enviarMensaje()
        }

        binding.botoncorreo.setOnClickListener {
            enviarEmail()
        }

        binding.botonUbicacion.setOnClickListener{
            verificarPermisoMapa()
        }
        binding.mapaImagen.setOnClickListener {
            verificarPermisoMapa()
        }
    }


    private fun verificarPermisoMapa() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            abrirMapa()
        } else {
            // Si no tenemos el permiso, lo solicitamos
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                lOCATIONPERMISSIONREQUESTCODE
            )

        }
    }



    private fun abrirMapa() {

        val latitude = "37.605973"
        val longitude = "-0.975306"
        val zoom = "15"
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($zoom)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        try {
            startActivity(mapIntent)
        } catch (_: Exception) {
            Snackbar.make(binding.root, appNoEncontrada, Snackbar.LENGTH_LONG).show()
        }
    }

    // Enviar mensaje WhatsApp
    private fun enviarMensaje() {
        val numeroTelefono = "+34 631782117"
        val mensaje = "¡Bienvenido! Contacta con nosotros en caso de cualquier duda."
        val irWhatsappUrl = "https://api.whatsapp.com/send?phone=$numeroTelefono&text=${Uri.encode(mensaje)}"
        val uri = Uri.parse(irWhatsappUrl)

        val wasIntent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(wasIntent)
        } catch (_: Exception) {
            Snackbar.make(binding.root, R.string.NOAPP, Snackbar.LENGTH_LONG).show()
        }
    }

    // Enviar correo
    private fun enviarEmail() {
        val destinatario = "fender@gmail.com"
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")  // Esto limita a las apps de correo
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
        }
        try {
            startActivity(emailIntent)
        } catch (_: Exception) {
            Snackbar.make(binding.root, R.string.NOAPP, Snackbar.LENGTH_LONG).show()
        }
    }

    // Méodo par llamada
    private fun hacerLlamada() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) ==
            PackageManager.PERMISSION_GRANTED) {

            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:+34 631782117"))
            //busca una app para hacer la accion
            if (intent.resolveActivity(requireContext().packageManager) != null){
                startActivity(intent)
            }
            else{
                Snackbar.make(binding.root, appNoEncontrada, Snackbar.LENGTH_LONG).show()
            }
        }else{ //si no tenemos el permiso se lo solicitamos al usuario
            //el usuario ya había rechazado el permiso anteriormente, entonces tiene que activarlo desde ajustes
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.CALL_PHONE )){
                Snackbar.make(binding.root, permisoDenegado, Snackbar.LENGTH_LONG).show()
            }else { //se pide el permiso

                // Si el permiso ya fue rechazado antes le aviso
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.CALL_PHONE)) {
                    Snackbar.make(binding.root, permisoDenegado, Snackbar.LENGTH_LONG).show()
                } else {// Si es la primera vez que se pide el permiso, se lo pido

                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(android.Manifest.permission.CALL_PHONE),
                        cALLPHONEPERMISSIONREQUEST
                    )
                }
            }
        }
    }

}