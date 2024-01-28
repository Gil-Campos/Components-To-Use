package com.example.otpview

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OptCodeViewModel : ViewModel() {

    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> get() = _timeLeft

    private var countDownTimer: CountDownTimer? = null

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

}