package com.example.viewpager2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.example.viewpager2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var dotState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeState()
    }

    private fun changeState() {
        binding.btnChangeState.setOnClickListener {
            dotState = !dotState

            if (dotState) {
                binding.dot1.background = resources.getDrawable(R.drawable.dot_background_finish)
                binding.dot3.background = resources.getDrawable(R.drawable.dot_forground_finish)

                binding.dot2.background =
                    resources.getDrawable(R.drawable.dot_background_finish)
                binding.dot4.background = resources.getDrawable(R.drawable.dot_forground_finish)
            } else {
                binding.dot1.background = resources.getDrawable(R.drawable.dot_background_not_finish)
                binding.dot3.background = resources.getDrawable(R.drawable.dot_forground_not_finish)

                binding.dot2.background =
                    resources.getDrawable(R.drawable.dot_background_not_finish)
                binding.dot4.background = resources.getDrawable(R.drawable.dot_forground_not_finish)
            }
        }
    }
}