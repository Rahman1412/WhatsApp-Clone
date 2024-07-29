package com.example.whatsappclone.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StatusVmFactory (val userId:String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatusVM(userId) as T
    }
}