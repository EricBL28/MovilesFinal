package com.example.aplicacionfinal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import com.example.aplicacionfinal.databinding.FragmentScaffoldBinding
import com.google.firebase.auth.FirebaseAuth


class ScaffoldFragment : Fragment() {
    private lateinit var binding: FragmentScaffoldBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScaffoldBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser?.email
        val header = binding.navigationView.getHeaderView(0)
        val textView: TextView= header.findViewById(R.id.textViewName)

        if (user != null){
            textView.text = user
        }else{
            textView.text = getString(R.string.usuario)
        }

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.atras -> {
                        logOut()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        /* DRAWERLAYOUT */
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_scaffold) as NavHostFragment
        val navController = navHostFragment.navController

        val toggle = ActionBarDrawerToggle(
            requireActivity(), binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener {

                item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.listaFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_dashboard -> {
                    navController.navigate(R.id.favoritosFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.nav_notifications -> {
                    navController.navigate(R.id.contactoFragment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_salir -> {
                    logOut()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }
        /* BOTTOM NAVIGATION MENU */
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bnm_home -> {
                    navController.navigate(R.id.tasFragment)
                    true
                }

                R.id.bnm_notifications -> {
                    navController.navigate(R.id.contactoFragment)
                    true
                }

                else -> false
            }
        }

    }

    private fun logOut() {
        // Después de cerrar sesión, redirigir al LoginActivity
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
        findNavController().navigate(R.id.action_ScaffolgFragment_to_LoginFragment)

        Toast.makeText(requireContext(), "Hasta luego ;)", Toast.LENGTH_SHORT).show()

    }


}