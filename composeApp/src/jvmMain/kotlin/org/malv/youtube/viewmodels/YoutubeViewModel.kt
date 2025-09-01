package org.malv.youtube.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sun.beans.introspect.PropertyInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.malv.youtube.services.CodesService
import org.malv.youtube.services.ConfigurationService
import org.malv.youtube.services.LoggerService
import org.malv.youtube.services.LoginService
import org.malv.youtube.services.YoutubeService
import java.io.File

class YoutubeViewModel(
    private val configuration: ConfigurationService,
    private val loggerService: LoggerService
) : ViewModel() {

    private val _clientSecretPath = MutableStateFlow(configuration.clientSecretPath)
    val clientSecretPath: StateFlow<String> = _clientSecretPath.asStateFlow()

    var loginService = LoginService(clientSecretPath.value, ConfigurationService.instance)

    private val _isLogged = MutableStateFlow(loginService.isLoggedIn())
    val isLogged: StateFlow<Boolean> = _isLogged.asStateFlow()

    private val _isFileSelected = MutableStateFlow(File(configuration.clientSecretPath).exists())
    val isFileSelected: StateFlow<Boolean> = _isFileSelected.asStateFlow()

    fun updateClientSecretPath(path: String) {
        _clientSecretPath.value = path
        configuration.clientSecretPath = path
        loginService = LoginService(path, ConfigurationService.instance)
        _isLogged.value = loginService.isLoggedIn()
        _isFileSelected.value = File(path).exists()
    }

    fun login() = viewModelScope.launch {
        loginService.requestLogin()
        _isLogged.value = loginService.isLoggedIn()
    }

    fun logout() = viewModelScope.launch {
        loginService.logout()
        _isLogged.value = loginService.isLoggedIn()
    }

    fun updateVideos() = viewModelScope.launch {
        val youtube = YoutubeService(loginService.getCredentials()!!)
        val videos = youtube.getVideos()
        val aliexpress = videos.filter { it.description.contains("aliexpress", true) }
        loggerService.clean()
        loggerService.info("Encontrados ${videos.distinctBy { it.id }.size} videos.\n")
        loggerService.info("Encontradas ${aliexpress.distinctBy { it.id }.size} descripciones de Aliexpress\n")
        loggerService.info("Actualizando descripciones...\n\n")

        val languages = configuration.languages.associateBy { it.code }

        var updated = 0
        var skipped = 0

        aliexpress.forEach { video ->
            val description = video.description
            loggerService.info("Comprobando ${video.title} (${video.id}): ")
            val language = languages[video.languageId] ?: let {
                loggerService.info("No se ha encontrado el idioma ${video.languageId} para el video ${video.title} (${video.id})")
                return@forEach
            }

            val newCodes = CodesService.generateCodes(configuration.codes, language.template)
            val codes = description
                .substringAfter(language.start, "")
                .substringBefore(language.end, "")

            when {
                codes.isBlank() -> {
                    loggerService.info("No se han encontrado «${language.start}» o «${language.end}» en la descripción\n")
                    loggerService.info("Edita la descripción para añadir los códigos: https://studio.youtube.com/video/${video.id}/edit\n\n")
                    loggerService.debug(video.description)
                }

                codes == newCodes -> {
                    skipped++
                    loggerService.info("No necesita actualizarse.\n")
                }

                else -> {
                    val newDescription = description.replace(codes, newCodes)
                    if (newDescription.length > 4999) {
                        loggerService.info("La descripción supera los 5000 caracteres, no se puede actualizar.\n")
                        loggerService.info("Edita la descripción: https://studio.youtube.com/video/${video.id}/edit\n\n")
                    } else {
                        updated++
                        loggerService.info("Actualizado.\n")
                        youtube.updateVideo(video.copy(description = newDescription))
                    }
                }
            }

        }

        loggerService.info("$updated descripciones actualizadas.\n")
        loggerService.info("$skipped descripciones sin actualizar.\n")
    }

}