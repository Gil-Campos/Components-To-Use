package com.example.otpview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.example.otpview.databinding.FragmentOtpCodeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpCodeFragment : Fragment() {

    private var optExternalCode = "12345"

    private var _binding: FragmentOtpCodeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OptCodeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOtpCodeRequest()
        observeUserAttempts()

        viewModel.createOptCode()
    }

    /**
     * Code to set up view
     */

    private fun formatTime(time: Long): String {
        val minutes = time / 60
        val seconds = time % 60
        return String.format("%01d:%02d", minutes, seconds)
    }

    private fun setUpTimer() {
        viewModel.timeLeft.observe(viewLifecycleOwner) {
            binding.timerText.text = formatTime(it)

            lockViewTimeExpired(it)
        }

        viewModel.startTimer(30000)
    }

    private fun setListener() {
        binding.constraintLayoutRoot.setOnClickListener {
            val inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(binding.constraintLayoutRoot.windowToken, 0)
        }

        setTextChangedListener(fromEditText = binding.etOne, targetEditText = binding.etTwo)
        setTextChangedListener(fromEditText = binding.etTwo, targetEditText = binding.etThree)
        setTextChangedListener(fromEditText = binding.etThree, targetEditText = binding.etFour)
        setTextChangedListener(fromEditText = binding.etFour, targetEditText = binding.etFive)
        setTextChangedListener(fromEditText = binding.etFive, done = {
            verifyOtpCode()
        })

        setKeyListener(fromEditText = binding.etTwo, backToEditText = binding.etOne)
        setKeyListener(fromEditText = binding.etThree, backToEditText = binding.etTwo)
        setKeyListener(fromEditText = binding.etFour, backToEditText = binding.etThree)
        setKeyListener(fromEditText = binding.etFive, backToEditText = binding.etFour)
    }

    private fun itemBackgroundEmptyState(editText: EditText) {
        editText.background =
            ResourcesCompat.getDrawable(resources, R.drawable.otp_item_empty_background, null)
    }

    private fun itemBackgroundNotEmptyState(editText: EditText) {
        editText.background =
            ResourcesCompat.getDrawable(resources, R.drawable.otp_item_not_empty_background, null)
    }

    private fun itemBackgroundErrorState(editText: EditText) {
        editText.background =
            ResourcesCompat.getDrawable(resources, R.drawable.otp_item_error_background, null)
    }

    private fun initFocus() {
        binding.etOne.isEnabled = true

        binding.etOne.postDelayed({
            binding.etOne.requestFocus()
            itemBackgroundNotEmptyState(binding.etOne)
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.etOne, 0)
        }, 500)
    }

    private fun reset() {
        binding.etOne.isEnabled = false
        binding.etTwo.isEnabled = false
        binding.etThree.isEnabled = false
        binding.etFour.isEnabled = false
        binding.etFive.isEnabled = false

        binding.etOne.setText("")
        binding.etTwo.setText("")
        binding.etThree.setText("")
        binding.etFour.setText("")
        binding.etFive.setText("")

        itemBackgroundEmptyState(binding.etOne)
        itemBackgroundEmptyState(binding.etTwo)
        itemBackgroundEmptyState(binding.etThree)
        itemBackgroundEmptyState(binding.etFour)
        itemBackgroundEmptyState(binding.etFive)
    }

    private fun setTextChangedListener(
        fromEditText: EditText,
        targetEditText: EditText? = null,
        done: (() -> Unit)? = null
    ) {
        fromEditText.addTextChangedListener {
            it?.let { string ->
                if (string.isNotEmpty()) {

                    targetEditText?.let { editText ->
                        editText.isEnabled = true
                        editText.requestFocus()
                        itemBackgroundNotEmptyState(editText)

                    } ?: run {
                        done?.let { done ->
                            done()
                        }
                    }

                    fromEditText.clearFocus()
                    fromEditText.isEnabled = false
                }
            }
        }
    }

    private fun setKeyListener(fromEditText: EditText, backToEditText: EditText) {
        fromEditText.setOnKeyListener { _, _, event ->

            if (null != event && KeyEvent.KEYCODE_DEL == event.keyCode) {
                backToEditText.isEnabled = true
                itemBackgroundNotEmptyState(backToEditText)
                backToEditText.requestFocus()
                backToEditText.setText("")

                itemBackgroundEmptyState(fromEditText)
                fromEditText.clearFocus()
                fromEditText.isEnabled = false
            }

            false
        }
    }

    private fun verifyOtpCode() {
        val otpCode = binding.etOne.text.toString().trim() + binding.etTwo.text.toString()
            .trim() + binding.etThree.text.toString().trim() + binding.etFour.text.toString()
            .trim() + binding.etFive.text.toString().trim()

        if (5 != otpCode.length) {
            return
        }

        if (otpCode == optExternalCode) {
            Toast.makeText(binding.root.context, "Success, should do next", Toast.LENGTH_SHORT)
                .show()

            val inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(binding.constraintLayoutRoot.windowToken, 0)

            return
        }

        itemBackgroundErrorState(binding.etOne)
        itemBackgroundErrorState(binding.etTwo)
        itemBackgroundErrorState(binding.etThree)
        itemBackgroundErrorState(binding.etFour)
        itemBackgroundErrorState(binding.etFive)

        viewModel.addUserCodeAttempt()

        Toast.makeText(binding.root.context, "Error, wrong code", Toast.LENGTH_SHORT).show()

        lockViewAttemptsExpired()
    }

    /**
     *  Lock view when time expires
     */

    private fun lockViewTimeExpired(time: Long) {
        if (time == 0L) {
            binding.timerText.text = formatTime(0)
            reset()
            binding.tvResendOtpCode.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.new_otp_code_active,
                    null
                )
            )
            binding.tvResendOtpCode.isEnabled = true
        }
    }

    /**
     * Start view for OTP code & lock view to request new OTP code
     */

    private fun startView() {
        if (viewModel.userAttempts.value == 1) {
            setListener()
        }

        initFocus()
        setUpTimer()
        listeners()


        binding.tvResendOtpCode.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.new_otp_code_inactive,
                null
            )
        )
        
        binding.tvResendOtpCode.isEnabled = false
    }

    private fun lockViewAttemptsExpired() {
        if (viewModel.userAttempts.value?.rem(4) != 0) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                reset()
                initFocus()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                viewModel.stopTimer()
                binding.timerText.text = formatTime(0)
                reset()
                binding.tvResendOtpCode.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.new_otp_code_active,
                        null
                    )
                )
                binding.tvResendOtpCode.isEnabled = true
            }
        }
    }

    private fun observeUserAttempts() {
        viewModel.userAttempts.observe(viewLifecycleOwner) {
            Log.d("USER_ATTEMPT", "Attempt: $it")
        }
    }

    /**
     * Code to observe new OTP code
     */

    private fun observeOtpCodeRequest() {
        viewModel.optExternalCode.observe(viewLifecycleOwner) {
            optExternalCode = it

            startView()

            Log.d("NEW_OTP_CODE", "Code: $it")
        }
    }

    /**
     * Listeners to trigger functions related to request of the new OTP code
     */

    private fun listeners() {
        requestNewOtpCode()
    }

    private fun requestNewOtpCode() {
        binding.tvResendOtpCode.setOnClickListener { viewModel.createOptCode() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}