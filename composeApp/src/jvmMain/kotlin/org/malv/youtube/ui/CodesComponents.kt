package org.malv.youtube.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.malv.youtube.models.Language
import org.malv.youtube.services.CodesService
import org.malv.youtube.services.ConfigurationService
import org.malv.youtube.viewmodels.CodesViewModel


@Composable
fun CodesInput(codes: String, onCodeChanges: (String) -> Unit) {
    CompactTextField(
        value = codes,
        onValueChange = onCodeChanges,
        maxLines = Int.MAX_VALUE,
        label = { Text("Códigos") },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CodesPreview(codes: String) {

    CompactTextField(
        value = codes,
        label = { Text("Resultado") },
        onValueChange = {},
        modifier = Modifier.sizeIn(minHeight = 400.dp).fillMaxSize(),
        maxLines = Int.MAX_VALUE,
    )

}


@Composable
fun CodesTemplateInput(template: String, onTemplateChanges: (String) -> Unit) {
    CompactTextField(
        value = template,
        onValueChange = onTemplateChanges,
        label = { Text("Plantilla") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StartCodes(start: String, onStartChanges: (String) -> Unit) {
    CompactTextField(
        value = start,
        onValueChange = onStartChanges,
        label = { Text("Inicio") },
        modifier = Modifier.fillMaxWidth(0.5f)
    )
}

@Composable
fun EndCodes(end: String, onEndChanges: (String) -> Unit) {
    CompactTextField(
        value = end,
        onValueChange = onEndChanges,
        label = { Text("Fin") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RangeCodes(start: String, end: String, onStartChanges: (String) -> Unit, onEndChanges: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        StartCodes(start, onStartChanges)
        EndCodes(end, onEndChanges)
    }
}


@Composable
fun Languages(
    languages: List<Language>,
    onUpdateLanguage: (Language) -> Unit,
    codes: String,
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    CompactCard(
        modifier =
            Modifier.fillMaxWidth(0.5f)
                .fillMaxHeight()
                .sizeIn(minHeight = 800.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            TabRow(selectedTabIndex = tabIndex) {
                languages.forEachIndexed { index, language ->
                    Tab(
                        text = { Text(language.title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            val language = languages[tabIndex]
            RangeCodes(
                language.start,
                language.end,
                onStartChanges = { onUpdateLanguage(language.copy(start = it)) },
                onEndChanges = { onUpdateLanguage(language.copy(end = it)) }
            )
            CodesTemplateInput(language.template, onTemplateChanges = { onUpdateLanguage(language.copy(template = it)) })
            CodesPreview(CodesService.generateCodes(codes = codes, language.template, language.start, language.end))
        }
    }
}

@Composable
fun CodesUI(
    codesViewModel: CodesViewModel = viewModel { CodesViewModel(ConfigurationService.instance) },
    modifier: Modifier = Modifier
) {
    val codes by codesViewModel.codes.collectAsState()
    val languages by codesViewModel.languages.collectAsState()

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier.fillMaxHeight()) {
        Languages(
            languages = languages,
            codes = codes,
            onUpdateLanguage = { updated ->
                codesViewModel.updateLanguages(languages.map { lang -> if (lang.code == updated.code) updated else lang })
            }
        )

        CompactCard(modifier = Modifier.fillMaxSize()) {
            CodesInput(codes, codesViewModel::updateCodes)
        }
    }
}