package com.ma7moud3ly.quran.features.recitation.config.verses

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.quran.model.asVerseNumber
import com.ma7moud3ly.quran.ui.AppTheme
import com.ma7moud3ly.quran.ui.DialogHeader
import com.ma7moud3ly.quran.ui.MyMiniDialog
import com.ma7moud3ly.quran.ui.MySurface
import com.ma7moud3ly.quran.ui.hafsSmartFamily
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import quran.composeapp.generated.resources.Res
import quran.composeapp.generated.resources.ok
import quran.composeapp.generated.resources.recite_select_begin
import quran.composeapp.generated.resources.recite_select_end

@Preview
@Composable
private fun VerseNumberDialogPreview() {
    AppTheme(darkTheme = true) {
        SelectVerseNumberDialog(
            start = remember { mutableStateOf(1) },
            end = remember { mutableStateOf(7) },
            limit = 7,
            selectStart = true,
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun VerseNumberDialogPreviewLight() {
    AppTheme(darkTheme = false) {
        SelectVerseNumberDialog(
            start = remember { mutableStateOf(1) },
            end = remember { mutableStateOf(7) },
            limit = 7,
            selectStart = true,
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectVerseNumberDialog(
    start: MutableState<Int>,
    end: MutableState<Int>,
    limit: Int,
    selectStart: Boolean,
    onDismiss: () -> Unit
) {

    val current by remember {
        derivedStateOf {
            when (selectStart) {
                true -> start.value
                false -> end.value
            }
        }
    }

    val startValue = remember(selectStart) {
        if (!selectStart) start.value
        else 1
    }

    val endValue = remember(selectStart) {
        if (selectStart) end.value
        else limit
    }

    var number by remember { mutableStateOf<Int?>(current) }
    var textNumber by remember { mutableStateOf(current.toString()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun incrementNumber() {
        if (number == null) number = current
        else if (number!! < endValue) number = number!! + 1
        textNumber = number.toString()
    }

    fun decrementNumber() {
        if (number == null) number = current
        else if (number!! > startValue) number = number!! - 1
        textNumber = number.toString()
    }

    fun resetToStaring() {
        number = startValue
        textNumber = startValue.toString()
    }

    fun resetToEnd() {
        number = endValue
        textNumber = endValue.toString()
    }

    fun getInput(value: String) {
        val newNumber = value.toIntOrNull()
        number = if (newNumber != null && newNumber <= endValue && newNumber >= startValue) {
            newNumber
        } else null
    }

    fun commit(value: Int) {
        if (selectStart) start.value = value
        else end.value = value
        if (start.value > end.value) end.value = limit
    }


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    MyMiniDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(16.dp),
        header = {
            DialogHeader(
                text = stringResource(
                    if (selectStart) Res.string.recite_select_begin
                    else Res.string.recite_select_end
                ),
                onBack = onDismiss
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ArrowButton(
                number = startValue,
                onClick = ::decrementNumber,
                onLongClick = ::resetToStaring
            )
            BasicTextField(
                value = textNumber,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { value ->
                    getInput(value)
                    textNumber = value

                },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
            ArrowButton(
                number = endValue,
                onClick = ::incrementNumber,
                onLongClick = ::resetToEnd
            )
        }
        Button(
            onClick = {
                if (number != null) commit(number!!)
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = number != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.ok),
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
            )
        }
    }
}


@Composable
private fun ArrowButton(
    number: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    MySurface(
        onClick = null,
        color = Color.Transparent,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Text(
            text = number.asVerseNumber(),
            fontFamily = hafsSmartFamily(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 50.sp
        )
    }
}
