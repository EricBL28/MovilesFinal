package com.example.aplicacionfinal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplicacionfinal.databinding.FragmentItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore


class GuitarraAdapter(private var guitarras: MutableList<Guitarra> ) : RecyclerView.Adapter<GuitarraAdapter.GuitarraMonitorViewHolder>() {

    private var itemsOriginal: List<Guitarra> = ArrayList(guitarras)
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuitarraMonitorViewHolder {
        val binding = FragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false
        )
        return GuitarraMonitorViewHolder(binding, auth)
    }

    override fun onBindViewHolder(holder: GuitarraMonitorViewHolder, position: Int) {
        holder.bind(guitarras[position])


    }
    override fun getItemCount(): Int = guitarras.size

    fun updateList(nuevaLista: List<Guitarra>) {
        guitarras.clear()
        guitarras.addAll(nuevaLista)
        itemsOriginal = ArrayList(nuevaLista) // Guardamos la lista original para filtrar
        notifyDataSetChanged()
    }

    //para cargar los monitores
    class GuitarraMonitorViewHolder(private val binding: FragmentItemBinding ,
                                    private var auth: FirebaseAuth) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Guitarra) {
            binding.itemTitle.text = data.nombre
            binding.itemDescription.text = data.precio

            // Cargar la imagen usando Glide
            Glide.with(binding.root.context)
                .load(data.imagen) // URL de la imagen
                .placeholder(R.drawable.guitarracarga) // Imagen mientras se carga
                .into(binding.itemImage) // imagen xml

            // Actualizar el ícono de favorito
            if (data.fav) {
                binding.favorito.setImageResource(R.drawable.fav_selected)
            } else {
                binding.favorito.setImageResource(R.drawable.fav_unselected)
            }

            // Configurar el clic en el botón de favoritos
            binding.favorito.setOnClickListener {
                data.fav = !data.fav
                if (data.fav) {
                    binding.favorito.setImageResource(R.drawable.fav_selected)
                } else {
                    binding.favorito.setImageResource(R.drawable.fav_unselected)
                }

                // Actualizar Firestore
                val db = Firebase.firestore
                db.collection("guitarras")
                    .document(data.id.toString())
                    .update("fav", data.fav)
                    .addOnSuccessListener {
                        // Actualizar la lista de favoritos del usuario en Firestore
                        val usuario = auth.currentUser?.email.toString()
                        val usuarioRef = db.collection("usuarios").document(usuario)

                        if (data.fav) {
                            // Añadir a favoritos
                            usuarioRef.update("fav", FieldValue.arrayUnion(data.id))
                        } else {
                            // Eliminar de favoritos
                            usuarioRef.update("fav", FieldValue.arrayRemove(data.id))
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Error al actualizar favorito: ${exception.message}")
                    }
            }
        }
    }
}


