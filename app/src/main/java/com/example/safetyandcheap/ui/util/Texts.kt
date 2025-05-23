package com.example.safetyandcheap.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
    )
}

@Composable
fun FilterText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight(500)
    )
}

@Composable
fun ExpandableText(
    initialText: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    collapsedLines: Int
) {
    var text by remember { mutableStateOf<AnnotatedString>(AnnotatedString(initialText)) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isExpanded by remember { mutableStateOf(false) }
    var isInit by remember { mutableStateOf(true) }

    LaunchedEffect(textLayoutResult) {
        textLayoutResult?.let { layoutResult ->
            if (isInit && layoutResult.lineCount <= collapsedLines) {
                text = AnnotatedString(initialText)
                isInit = false
                return@let
            }
            if (!isExpanded) {

                val lineEndIndex = layoutResult.getLineEnd(
                    if (collapsedLines <= layoutResult.lineCount) collapsedLines - 1 else layoutResult.lineCount - 1
                )
                text = buildAnnotatedString {
                    append(initialText.substring(0, lineEndIndex - 8))
                    append("... ")
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = Color(0xFF5762FF)
                        )
                    ) {
                        append("More")
                    }
                    addLink(
                        LinkAnnotation.Clickable(
                            "MoreTag",
                            linkInteractionListener = {
                                isExpanded = true
                            }),
                        start = lineEndIndex - 4,
                        end = lineEndIndex
                    )
                }
            } else {
                text = buildAnnotatedString {
                    append(initialText)
                    append(" ")
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = Color(0xFF5762FF)
                        )
                    ) {
                        append("Less")
                    }
                    addLink(
                        LinkAnnotation.Clickable(
                            "LessTag",
                            linkInteractionListener = {
                                isExpanded = false
                            }),
                        start = initialText.length + 1,
                        end = initialText.length + 5
                    )
                }
            }
            isInit = false
        }
    }
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        overflow = TextOverflow.Clip,
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult
        },
        color = MaterialTheme.colorScheme.tertiary
    )
}