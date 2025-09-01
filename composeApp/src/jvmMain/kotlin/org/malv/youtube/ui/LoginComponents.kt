package org.malv.youtube.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.malv.youtube.services.ConfigurationService
import org.malv.youtube.services.LoggerService
import org.malv.youtube.viewmodels.YoutubeViewModel


@Composable
fun ButtonClientSecret(onPathChanges: (String) -> Unit) {
    CompactButton(onClick = {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.FILES_ONLY
        chooser.fileFilter = FileNameExtensionFilter("JSON file", "json")
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            onPathChanges(chooser.selectedFile.absolutePath)
        }

    }) {
        Text("Select client_secret.json")
    }
}

@Composable
fun LoginButton(onClick: () -> Unit = {}) {
    CompactButton(onClick = onClick) {
        Text("Youtube Login")
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit = {}) {
    CompactButton(onClick = onClick) {
        Text("Youtube Logout")
    }
}

@Composable
fun UpdateVideosButton(onClicked: () -> Unit) {
    CompactButton(onClick = onClicked) {
        Text("Update Videos")
    }
}

@Composable
fun SaveButton(onClicked: () -> Unit) {
    CompactButton(onClick = onClicked) {
        Text("Save")
    }
}



@Composable
fun ButtonsUI(
    youtubeViewModel: YoutubeViewModel = viewModel { YoutubeViewModel(ConfigurationService.instance, LoggerService.instance) }
) {
    val isLogged by youtubeViewModel.isLogged.collectAsState()
    val isFileSelected by youtubeViewModel.isFileSelected.collectAsState()
    val showLogin = !isLogged && isFileSelected
    val showLogout = isLogged

    CompactCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ButtonClientSecret(onPathChanges = { youtubeViewModel.updateClientSecretPath(it) })
                if (showLogin) LoginButton { youtubeViewModel.login() }
                if (showLogout) {
                    LogoutButton { youtubeViewModel.logout() }
                    UpdateVideosButton { youtubeViewModel.updateVideos() }
                }

            }
        }
    }
}