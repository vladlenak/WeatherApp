package t.me.octopusapps.weatherapp.ui.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import t.me.octopusapps.weatherapp.databinding.FragmentLocationBinding
import t.me.octopusapps.weatherapp.ui.viewmodels.LocationState
import t.me.octopusapps.weatherapp.ui.viewmodels.LocationViewModel

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LocationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.locationState.collect { state ->
                when (state) {
                    is LocationState.Idle -> {}
                    is LocationState.PermissionRequired -> {
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
                    }
                    is LocationState.LocationFetched -> {
                        navigateToHomeFragment(state.latitude, state.longitude)
                    }
                    is LocationState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnCheckLocation.setOnClickListener {
            viewModel.checkLocationPermission(requireContext())
        }

        binding.btnCheckCity.setOnClickListener {
            val cityName = binding.etSearchCity.text.toString()
            if (cityName.isNotBlank()) {
                viewModel.fetchCityCoordinates(cityName)
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun navigateToHomeFragment(latitude: Double, longitude: Double) {
        val action = LocationFragmentDirections.actionLocationFragmentToHomeFragment(
            latitude.toString(),
            longitude.toString()
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
