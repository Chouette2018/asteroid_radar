package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Filter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(requireActivity().application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val binding = FragmentMainBinding.inflate(inflater)

        val asteroidListener = AsteroidListener{asteroid -> viewModel.onAsteroidClicked(asteroid)}
        val adapter = AsteroidAdapter(asteroidListener)
        binding.apply{
            asteroidRecycler.adapter = adapter

            boundViewModel = viewModel
            viewModel.asteroids.observe(viewLifecycleOwner, Observer {
                it?.let{
                    adapter.submitList(it)
                }
            })

            viewModel.navigateToAsteroidDetails.observe(viewLifecycleOwner, Observer{
                asteroid -> asteroid?.let{
                    findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                    viewModel.navigateToAsteroidDetailsDone()
                }
            })


            lifecycleOwner = this@MainFragment

            activityMainImageOfTheDay.contentDescription = getString(R.string.image_of_the_day)

            return binding.root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.show_saved_menu -> viewModel.updateFilter(Filter.SHOW_SAVED)
            R.id.show_today_menu -> viewModel.updateFilter(Filter.SHOW_TODAY)
            else -> viewModel.updateFilter(Filter.SHOW_ALL)
        }
        return true
    }
}
