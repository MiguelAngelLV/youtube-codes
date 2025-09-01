package org.malv.youtube.models

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val language: String,
    val single: Boolean
) {

    val languageId: String
        get() = when {
            language.startsWith("en", ignoreCase = true) -> "en-US"
            language.startsWith("es", ignoreCase = true) -> "es-ES"
            else -> "en_US"
        }
}