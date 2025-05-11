package com.example.virtualwing.viewmodel.squadron

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.virtualwing.repository.SquadronRepository

class SquadronViewModelFactory(private val squadronRepository: SquadronRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return SquadronViewModel(squadronRepository) as T
    }
}