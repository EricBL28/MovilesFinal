package com.example.aplicacionfinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.aplicacionfinal.databinding.FragmentListaBinding
import com.google.firebase.Firebase

class ListaFragment : Fragment() {
    private lateinit var binding: FragmentListaBinding

    private var monitores = mutableListOf<Guitarra>()


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

}