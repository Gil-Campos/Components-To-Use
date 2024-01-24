package com.example.autocompletetextview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.autocompletetextview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpAutoCompleteTextView()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpAutoCompleteTextView() {
        val stringArr = resources.getStringArray(R.array.languages)

        val arrayAdapter = ArrayAdapter(this, R.layout.drop_down_item, stringArr)

        binding.autoCompleteTextView.setDropDownBackgroundDrawable(this.getDrawable(R.drawable.no_padding_dropdown))
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
    }
}