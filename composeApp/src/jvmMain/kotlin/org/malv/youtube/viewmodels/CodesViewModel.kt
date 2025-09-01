package org.malv.youtube.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.malv.youtube.models.Language
import org.malv.youtube.services.ConfigurationService

class CodesViewModel(private val configuration: ConfigurationService) : ViewModel() {

    private val _codes = MutableStateFlow(configuration.codes)
    val codes: StateFlow<String> = _codes.asStateFlow()

    private val _languages = MutableStateFlow(configuration.languages)
    val languages: StateFlow<List<Language>> = _languages.asStateFlow()

    fun updateCodes(input: String) {
        _codes.value = input
        configuration.codes = input
    }

    fun updateLanguages(languages: List<Language>) {
        configuration.languages = languages
        _languages.value = languages
    }
}