package com.example.bottomsheetdialog.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window.FEATURE_NO_TITLE
import androidx.fragment.app.activityViewModels
import com.example.bottomsheetdialog.databinding.FragmentNewTaskSheetBinding
import com.example.bottomsheetdialog.viewmodel.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewTaskSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNewTaskSheetBinding? = null
    private val binging get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binging.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keepDialog()
        listeners()
    }

    private fun listeners() {
        binging.saveButton.setOnClickListener {
            saveAction()
        }
    }

    private fun saveAction() {
        viewModel.setName(binging.name.text.toString())
        viewModel.setDesc(binging.desc.text.toString())
        binging.name.setText("")
        binging.desc.setText("")
        dismiss()
    }

    private fun keepDialog() {
        dialog?.window?.requestFeature(FEATURE_NO_TITLE)
        dialog?.window?.setDimAmount(0f)
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)

        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}