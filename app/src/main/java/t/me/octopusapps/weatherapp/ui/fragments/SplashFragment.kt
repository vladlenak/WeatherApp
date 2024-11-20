package t.me.octopusapps.weatherapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import t.me.octopusapps.weatherapp.R
import t.me.octopusapps.weatherapp.databinding.FragmentSplashBinding

@SuppressLint("CustomSplashScreen")
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        navigateAfterDelay()

        return binding.root
    }

    private fun navigateAfterDelay() {
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(
                R.id.action_splashFragment_to_locationFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.fragment_splash, true)
                    .build()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
