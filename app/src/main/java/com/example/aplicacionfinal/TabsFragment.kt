package com.example.aplicacionfinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aplicacionfinal.databinding.FragmentTabsBinding

import com.google.android.material.tabs.TabLayoutMediator

class TabsFragment : Fragment() {

    private lateinit var binding: FragmentTabsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentTabsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(requireActivity())

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout , binding.viewPager)
        { tab, position -> tab.text =
            when (position) {
                0-> "Lista Guitarras"
                1-> "Favoritos"
                else -> ""
            }

        }.attach()
    }
    class ViewPagerAdapter (fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 2 //hay 2 tabs
        override fun createFragment(position: Int): Fragment {

            return when (position) {
                0 ->ListaFragment()
                1 ->FavoritosFragment()
                else ->ListaFragment()
            }
        }
    }
}