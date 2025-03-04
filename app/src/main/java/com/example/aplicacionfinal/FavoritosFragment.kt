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
import com.example.aplicacionfinal.databinding.FragmentFavoritosBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class FavoritosFragment : Fragment() {

    private lateinit var binding: FragmentFavoritosBinding
    private lateinit var adapter: GuitarraAdapter
    private var guitarras = mutableListOf<Guitarra>()
    private var db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritosBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewFAV.visibility = View.GONE
        binding.progressBarFAV.visibility = View.VISIBLE
        db= FirebaseFirestore.getInstance()
        guitarras = mutableListOf()

        adapter = GuitarraAdapter(guitarras)
        binding.recyclerViewFAV.adapter = adapter

        mostraRecyclerView()
        setupSwipeRefresh()
        cargarFavoritos()
    }

    // Cuando el Fragment se reanuda, actualizamos la lista de monitores
    override fun onResume() {
        super.onResume()
        cargarFavoritos()
    }
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayoutFAV.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                cargarFavoritos()
                binding.swipeRefreshLayoutFAV.isRefreshing = false
            }, 1000)
        }
    }


    private fun cargarFavoritos() {
        guitarras.clear()
        binding.recyclerViewFAV.visibility = View.GONE
        binding.progressBarFAV.visibility = View.VISIBLE

        db.collection("guitarras")
            .whereEqualTo("fav", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val guitarra = Guitarra(
                        document.id.toInt(),
                        document.getString("nombre") ?: "",
                        document.getString("precio") ?: "",
                        document.getBoolean("fav") == true,
                        document.getString("url") ?: ""
                    )

                    guitarras.add(guitarra)
                }

                binding.progressBarFAV.visibility = View.GONE
                binding.recyclerViewFAV.visibility = View.VISIBLE

                // adapter.updateList(listaFavoritos)
                adapter = GuitarraAdapter(guitarras)
                mostraRecyclerView()
            }
            .addOnFailureListener { _ ->
                binding.progressBarFAV.visibility = View.GONE //por si falla que no se quede alli
                Toast.makeText(requireContext(), "Error cargando las guitarras", Toast.LENGTH_SHORT).show()
            }
    }
    private fun mostraRecyclerView() {
        binding.recyclerViewFAV.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFAV.adapter = adapter
    }
}