package com.example.otpview

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OptCodeViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> get() = _timeLeft

    private var countDownTimer: CountDownTimer? = null

    private val _userAttempts: MutableLiveData<Int> = MutableLiveData(1)

    val userAttempts: LiveData<Int> get() = _userAttempts

    private val _optExternalCode: MutableLiveData<String> = MutableLiveData("")

    val optExternalCode: LiveData<String> get() = _optExternalCode

    fun startTimer(initialTime: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(initialTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.postValue(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                _timeLeft.postValue(0)
            }

        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel() // Ensure timer is cancelled when ViewModel is cleared
    }

    /**
     * Code to create new OTP code
     */

    fun createOptCode() {
        var newTokenOtp = ""

        for (i in 1..5) {
            val randomInt = (1..9).random()
            newTokenOtp += randomInt.toString()
        }

        _optExternalCode.value = newTokenOtp
    }

    /**
     * Code to count how many times the user has failed to enter the OTP code
     */

    fun addUserCodeAttempt() {
        _userAttempts.value = _userAttempts.value?.plus(1)
    }

}