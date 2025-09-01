package org.malv.youtube.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File
import java.io.FileReader
import java.net.URI


class LoginService(
    private val file: String,
    private val configurationService: ConfigurationService
) {

    private var flow = getFlow()

    private fun getFlow(): GoogleAuthorizationCodeFlow? {
        val httpTransport = NetHttpTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val clientSecretsFile = File(file)

        if (!clientSecretsFile.exists()) return null

        val dataDirectory = configurationService.getDirectory(CONFIG)
        dataDirectory.mkdirs()

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, FileReader(clientSecretsFile))

        return GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets,
            listOf("https://www.googleapis.com/auth/youtube.force-ssl")
        ).setDataStoreFactory(FileDataStoreFactory(dataDirectory)).build()
    }

    fun getCredentials(): Credential? {
        return flow?.loadCredential("user")?.takeIf { it.accessToken != null }
    }

    fun getAuthorizationUrl(): String {
        val receiver = LocalServerReceiver.Builder().setPort(8080).build()

        val url = flow?.newAuthorizationUrl()
            ?.setRedirectUri(receiver.redirectUri)
            ?.build() ?: ""
        receiver.stop()
        return url
    }

    suspend fun requestLogin(): Credential = withContext(Dispatchers.IO){
        val url = getAuthorizationUrl()
        openInBrowser(URI.create(url))
        val receiver = LocalServerReceiver.Builder().setPort(8080).build()
        AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun isLoggedIn(): Boolean {
        if (!File(file).exists()) return false
        return getCredentials()?.accessToken != null
    }

    fun logout() {
        flow?.credentialDataStore?.delete("user")
    }


    private fun openInBrowser(uri: URI) {
        val osName by lazy(LazyThreadSafetyMode.NONE) { System.getProperty("os.name").lowercase() }
        val desktop = Desktop.getDesktop()
        when {
            Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) -> desktop.browse(uri)
            "mac" in osName -> Runtime.getRuntime().exec(arrayOf("open", "$uri"))
            "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec(arrayOf("xdg-open", "$uri"))
            else -> throw RuntimeException("cannot open $uri")
        }
    }

    companion object {
        const val CONFIG = "credentials"
    }

}