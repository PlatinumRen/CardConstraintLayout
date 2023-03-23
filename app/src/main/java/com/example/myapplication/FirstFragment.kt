package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.viewmodel.LoadViewModel
import com.example.myapplication.widget.DisplayRecyclerView
import com.example.myapplication.widget.StaggerPager
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    loadViewModel.pictures.collect {
                        val displayAdapter = binding.pictureDisplay.adapter
                        (displayAdapter as DisplayRecyclerView.DisplayItemAdapter).updateItem(
                            it.toArrayList()
                        )
                        val staggerAdapter = binding.pictureStagger.adapter
                        (staggerAdapter as StaggerPager.StaggerItemAdapter).updateItem(
                            it.toArrayList()
                        )
                    }
                }
                launch {
                    binding.pictureDisplay.callback = object : DisplayRecyclerView.CardCallback {
                        override fun forward(position: Int) {
                            Log.d("NEU", "forward: ")

                            if (position == 1) {
                                val set = ConstraintSet()
                                set.clone(binding.cardConstraint)
                                set.connect(
                                    binding.pictureDisplay.id,
                                    ConstraintSet.START,
                                    binding.cardConstraint.id,
                                    ConstraintSet.START,
                                    binding.root.resources.getDimension(R.dimen.display_view_with_stack_margin_start).toInt()
                                )
                                set.applyTo(binding.cardConstraint)
                            }

                            val staggerAdapter = binding.pictureStagger.adapter
                            (staggerAdapter as StaggerPager.StaggerItemAdapter).setPosition(position)
                        }

                        override fun backward(position: Int) {
                            Log.d("NEU", "backward: ")

                            if (position == 0) {
                                val set = ConstraintSet()
                                set.clone(binding.cardConstraint)
                                set.connect(
                                    binding.pictureDisplay.id,
                                    ConstraintSet.START,
                                    binding.cardConstraint.id,
                                    ConstraintSet.START,
                                    binding.root.resources.getDimension(R.dimen.display_view_without_stack_margin_start).toInt()
                                )
                                set.applyTo(binding.cardConstraint)
                            }

                            val staggerAdapter = binding.pictureStagger.adapter
                            (staggerAdapter as StaggerPager.StaggerItemAdapter).setPosition(position)
                        }
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("NEU", "onViewCreated: ")
    }
}