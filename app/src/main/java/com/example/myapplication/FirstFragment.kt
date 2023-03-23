package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.viewmodel.LoadViewModel
import com.example.myapplication.widget.CardState
import com.example.myapplication.widget.StaggerShared
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    private val loadViewModel by viewModels<LoadViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFirstBinding.inflate(inflater).apply {
            loadVm = loadViewModel
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    loadViewModel.pictures.collect {
                        binding.pictureDisplay.apply {
                            updateItem(it)
                        }
                    }
                }
                launch {
                    binding.pictureDisplay.getStateFlow().collect {
                        Log.d("RRR", "pictureDisplay state: $it")

                        when (it) {
                            CardState.Idle -> {
                                // Noop.
                            }
                            is CardState.Forward -> {
                                binding.pictureStagger.addPicture(it.bitmap)
                            }
                            is CardState.Backward -> {
                                binding.pictureStagger.removePicture(it.bitmap)
                            }
                            is CardState.ShowComment -> {
                                loadViewModel
                                binding.pictureDisplay.showComment(it.position)
                            }
                            CardState.Like -> {
                                // Noop.
                            }
                        }
                    }
                }
                launch {
                    binding.pictureStagger.getSharedFlow().collect {
                        when (it) {
                            is StaggerShared.Open -> {
                                binding.pictureDisplay.adjustLayout(it.position + 1, true)
                            }
                            is StaggerShared.Close -> {
                                binding.pictureDisplay.adjustLayout(it.position + 1, false)
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RRR", "onViewCreated: ")
    }
}