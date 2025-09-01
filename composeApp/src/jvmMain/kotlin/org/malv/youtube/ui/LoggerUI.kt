package org.malv.youtube.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import org.malv.youtube.services.LoggerService
import org.malv.youtube.viewmodels.LoggerViewModel


@Composable
fun LoggerUI(viewmodel: LoggerViewModel = viewModel { LoggerViewModel(LoggerService.instance) }) {

    val log by viewmodel.log.collectAsState()
    val scrollState = rememberScrollState()

    CompactCard {

        OutlinedTextField(
            value = log,
            textStyle = MaterialTheme.typography.bodySmall,
            readOnly = true,
            onValueChange = {},
            label = { Text("Logs") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Gray,
                disabledLabelColor = Color.Gray,
                disabledBorderColor = Color.Gray,
            ),
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
        )
    }

    LaunchedEffect(log) {
        scrollState.scrollTo(scrollState.maxValue)
    }

}