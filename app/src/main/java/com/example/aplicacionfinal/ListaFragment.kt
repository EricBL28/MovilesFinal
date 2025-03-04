package com.example.aplicacionfinal

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionfinal.databinding.FragmentListaBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListaFragment : Fragment() {

    private lateinit var binding: FragmentListaBinding
    private lateinit var adapter: GuitarraAdapter
    private var guitarras = mutableListOf<Guitarra>()
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentListaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewGuitarraLista.visibility= View.GONE

        binding.progressBar.visibility = View.VISIBLE

        // Inicializamos el RecyclerView con una lista vacía
        setupRecyclerView()
        //swipe de recarga
        binding.swipeRefreshLayout.setOnRefreshListener {

            Handler(Looper.getMainLooper()).postDelayed({
                // Recargar los datos
                obtenerGuitarras()
                // Detener la animación de carga
                binding.swipeRefreshLayout.isRefreshing = false
            }, 1000) // 1 segundos
        }

        // Cargamos los datos de Firestore
        obtenerGuitarras()
    }

    private fun obtenerGuitarras() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewGuitarraLista.visibility = View.GONE

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000) // Esperar 1 segundo
            db.collection("guitarras")
                .get()
                .addOnSuccessListener { documents ->
                    guitarras.clear()
                    for (document in documents) {
                        val guitarra = Guitarra(
                            document.id.toInt(),
                            document.get("nombre") as String,
                            document.get("precio") as String,
                            document.get("fav") as Boolean,
                            document.get("url") as String,
                        )
                        guitarras.add(guitarra)
                    }

                    adapter.updateList(guitarras)

                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewGuitarraLista.visibility = View.VISIBLE
                }
                .addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error cargando las guitarras", Toast.LENGTH_SHORT).show()
                }
        }
    }




    private fun setupRecyclerView() {
        adapter = GuitarraAdapter(mutableListOf()) // Inicialmente vacío
        binding.recyclerViewGuitarraLista.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewGuitarraLista.adapter = adapter
    }

}