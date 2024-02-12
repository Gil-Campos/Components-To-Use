package com.example.bottomsheetdialog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel: ViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _desc = MutableLiveData<String>()
    val desc: LiveData<String> get() = _desc

    fun setName(name: String) {
        _name.value = name
    }

    fun setDesc(description: String) {
        _desc.value = description
    }
}