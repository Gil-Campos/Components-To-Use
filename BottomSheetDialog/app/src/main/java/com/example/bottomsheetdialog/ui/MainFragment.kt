package com.example.bottomsheetdialog.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.bottomsheetdialog.R
import com.example.bottomsheetdialog.databinding.FragmentMainBinding
import com.example.bottomsheetdialog.viewmodel.TaskViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()
    private val bottomSheetFragment = NewTaskSheetFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        triggerBottomSheetDialog()
        setUpObservers()
        displayBottomSheetDialog()
    }

    private fun triggerBottomSheetDialog() {
        bottomSheetFragment.show(parentFragmentManager, "NewTaskSheetFragment_TAG")
    }

    private fun displayBottomSheetDialog() {
        binding.newTaskButton.setOnClickListener {
            triggerBottomSheetDialog()
        }
    }

    private fun setUpObservers() {
        viewModel.name.observe(viewLifecycleOwner) {
            binding.taskName.text = String.format("Task Name: %s", it)
        }

        viewModel.desc.observe(viewLifecycleOwner) {
            binding.taskDesc.text = String.format("Task Desc: %s", it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}