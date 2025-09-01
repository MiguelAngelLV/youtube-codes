package org.malv.youtube.viewmodels

import androidx.lifecycle.ViewModel
import org.malv.youtube.services.LoggerService


class LoggerViewModel(
    private val loggerService: LoggerService
) : ViewModel(){

    val log = loggerService.log

}