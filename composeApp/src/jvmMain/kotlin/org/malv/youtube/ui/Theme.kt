package org.malv.youtube.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// CompositionLocals para paddings globales
val LocalButtonContentPadding = compositionLocalOf { PaddingValues(horizontal = 8.dp, vertical = 4.dp) }
val LocalTextFieldPadding = compositionLocalOf { PaddingValues(horizontal = 1.dp, vertical = 1.dp) }

private val CompactTypography = Typography(
    bodyLarge = TextStyle(fontSize = 13.sp),
    bodyMedium = TextStyle(fontSize = 12.sp),
    labelLarge = TextStyle(fontSize = 11.sp),
    labelMedium = TextStyle(fontSize = 10.sp),
)

private val CompactShapes = Shapes(
    small = RoundedCornerShape(3.dp),
    medium = RoundedCornerShape(3.dp),
    large = RoundedCornerShape(4.dp),
    extraLarge = RoundedCornerShape(5.dp),
    extraSmall = RoundedCornerShape(3.dp),
)

private val CompactColors = lightColorScheme()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTheme(content: @Composable () -> Unit) {

    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 8.dp,
        LocalButtonContentPadding provides PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        LocalTextFieldPadding provides PaddingValues(horizontal = 6.dp, vertical = 2.dp),
    ) {
        MaterialTheme(
            colorScheme = CompactColors,
            typography = CompactTypography,
            shapes = CompactShapes,
            content = content
        )
    }
}


@Composable
fun CompactButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        content = content,
        contentPadding = LocalButtonContentPadding.current
    )
}

@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLines: Int = 1,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        maxLines = maxLines,
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = modifier,
        label = label,
        enabled = enabled,
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.Gray,
            disabledLabelColor = Color.Gray,
            disabledBorderColor = Color.Gray,
        ),


    )
}

@Composable
fun CompactCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
       Box(modifier = Modifier.padding(8.dp)) {
           content()
       }
    }
}